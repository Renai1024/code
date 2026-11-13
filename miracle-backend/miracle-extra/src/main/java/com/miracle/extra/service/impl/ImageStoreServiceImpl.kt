package com.miracle.extra.service.impl

import com.miracle.common.utils.SecurityUtils
import com.miracle.common.utils.uuid.IdUtils
import com.miracle.extra.config.MinioConfig
import com.miracle.extra.domain.ImageStore
import com.miracle.extra.domain.enums.BucketType
import com.miracle.extra.mapper.ImageStoreMapper
import com.miracle.extra.resolver.MinioBucketResolver
import com.miracle.extra.service.IImageStoreService
import com.miracle.extra.utils.ImageCompression
import io.minio.BucketExistsArgs
import io.minio.GetObjectArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.http.Method
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.TimeUnit

@Service
open class ImageStoreServiceImpl(
    private val mapper: ImageStoreMapper,
    private val client: MinioClient,
    private val config: MinioConfig,
    private val resolver: MinioBucketResolver,
    private val references: ImageReferenceService,
    private val lifecycle: ImageLifecycleWorker,
    private val compression: ImageCompression
) : IImageStoreService {
    private val log = LoggerFactory.getLogger(javaClass)

    // 历史实现保留：2026-10-14 改为业务事务与 outbox 驱动。
    //     override fun uploadImage(file: MultipartFile, businessType: BucketType): ImageStore {
    //         val image = compression.compress(file, businessType)
    //         val bucket = resolver.resolve(businessType)
    //         require(bucket.isNotBlank()) { "未配置图片 bucket" }
    //         check(client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
    //             "图片 bucket 不存在：$bucket"
    //         }
    // 
    //         val prefix = when (businessType) {
    //             BucketType.MATERIAL -> "material"
    //             BucketType.PROD_ORDER -> "prod-order"
    //             BucketType.AVATAR -> "avatar"
    //             BucketType.GENERAL -> "general"
    //         }
    //         val day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    //         val imageName = "${IdUtils.fastUUID()}.${image.extension()}"
    //         val key = "$prefix/$day/$imageName"
    //         val bytes = image.bytes()
    // 
    //         client.putObject(
    //             PutObjectArgs.builder().bucket(bucket).`object`(key)
    //                 .stream(ByteArrayInputStream(bytes), bytes.size.toLong(), -1)
    //                 .contentType(image.contentType()).build()
    //         )
    // 
    //         try {
    //             val record = ImageStore().apply {
    //                 this.imageName = imageName
    //                 this.originalName = file.originalFilename ?: imageName
    //                 this.imagePath = key
    //                 this.imageUrl = "${config.endpoint.trimEnd('/')}/$bucket/$key"
    //                 this.imageSize = bytes.size.toLong()
    //                 this.imageType = image.contentType()
    //                 this.bucketName = bucket
    //                 this.status = 0
    //                 this.createBy = SecurityUtils.getLoginUser().user.nickName
    //                 this.createTime = Date()
    //             }
    //             check(mapper.insertImageStore(record) == 1) { "图片元数据写入失败" }
    //             return record
    //         } catch (failure: Exception) {
    //             try {
    //                 client.removeObject(RemoveObjectArgs.builder().bucket(bucket).`object`(key).build())
    //             } catch (cleanup: Exception) {
    //                 log.error("上传失败后清理对象失败，bucket={} key={}", bucket, key, cleanup)
    //                 failure.addSuppressed(cleanup)
    //             }
    //             throw failure
    //         }
    //     }
    /** 新增：先落暂存元数据，再上传两份对象；失败保留可重试的删除定位信息。 */
    override fun uploadImage(file: MultipartFile, businessType: BucketType): ImageStore {
        // 历史说明：曾在仅物料模式禁止创建工单图片，现已由业务路由替代。
        // 历史仅物料限制：require(businessType != BucketType.PROD_ORDER || config.orderImagesEnabled) { "当前部署未启用工单图片功能" }
        // 新增：工单上传恢复正常能力，引用清理按对象业务隔离。
        val image = compression.compress(file, businessType)
        val thumb = compression.thumbnail(image)
        val bucket = resolver.resolve(businessType)
        require(bucket.isNotBlank()) { "未配置图片 bucket" }
        check(client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) { "图片 bucket 不存在" }
        val prefix = businessType.name.lowercase().replace('_', '-')
        val day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        val name = "${IdUtils.fastUUID()}.${image.extension()}"
        val record = ImageStore().apply {
            imageName = name; originalName = file.originalFilename ?: name
            imagePath = "$prefix/$day/$name"; thumbnailPath = "$prefix/$day/thumb/$name"
            bucketName = bucket; imageSize = image.bytes().size.toLong(); imageType = image.contentType()
            status = 2; ownerUserId = SecurityUtils.getUserId()
            expiresAt = Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24))
            createBy = SecurityUtils.getUsername(); createTime = Date()
            // 兼容旧 URL 列，只保存稳定对象地址；返回值另外换成签名地址。
            imageUrl = "${config.endpoint.trimEnd('/')}/$bucket/$imagePath"
        }
        check(mapper.insertImageStore(record) == 1) { "图片元数据写入失败" }
        try {
            put(bucket, record.imagePath!!, image)
            put(bucket, record.thumbnailPath!!, thumb)
            record.imageUrl = getImagePreviewUrl(record.imageId!!)
            return record
        } catch (failure: Exception) {
            try { mapper.markImageDeleting(record.imageId!!); lifecycle.processDelete(record.imageId!!) }
            catch (cleanup: Exception) { log.error("上传补偿待后台重试 id={}", record.imageId, cleanup); failure.addSuppressed(cleanup) }
            throw failure
        }
    }

    /** 两份对象统一使用压缩结果的真实 MIME 类型。 */
    private fun put(bucket: String, key: String, image: ImageCompression.ProcessedImage) {
        val bytes = image.bytes()
        client.putObject(PutObjectArgs.builder().bucket(bucket).`object`(key)
            .stream(ByteArrayInputStream(bytes), bytes.size.toLong(), -1).contentType(image.contentType()).build())
    }

    // 历史实现保留：2026-10-14 改为业务事务与 outbox 驱动。
    //     override fun downloadImage(imageId: Long): InputStream {
    //         val record = mapper.selectImageStoreById(imageId)
    //             ?: throw IllegalArgumentException("图片不存在")
    //         require(record.status == 0) { "图片不可下载" }
    //         return client.getObject(GetObjectArgs.builder()
    //             .bucket(storedBucket(record))
    //             .`object`(record.imagePath ?: error("图片缺少对象 key")).build())
    //     }
    /** 新增：下载已确认图片前校验业务关联及现有模块权限。 */
    override fun downloadImage(imageId: Long): InputStream {
        val record = readable(imageId, false)
        return client.getObject(GetObjectArgs.builder().bucket(storedBucket(record)).`object`(record.imagePath!!).build())
    }

    // 历史实现保留：2026-10-14 改为业务事务与 outbox 驱动。
    //     override fun getImagePreviewUrl(imageId: Long): String {
    //         val record = mapper.selectImageStoreById(imageId)
    //             ?: throw IllegalArgumentException("图片不存在")
    //         require(record.status == 0) { "图片不可预览" }
    //         return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
    //             .method(Method.GET)
    //             .bucket(storedBucket(record))
    //             .`object`(record.imagePath ?: error("图片缺少对象 key"))
    //             .expiry(1, TimeUnit.HOURS).build())
    //     }
    /** 新增：本人暂存图允许编辑预览，已提交关联但待确认的业务图也按模块读取权限授权。 */
    override fun getImagePreviewUrl(imageId: Long): String = signed(readable(imageId, true), false)

    private fun readable(id: Long, pending: Boolean): ImageStore {
        val image = mapper.selectImageStoreById(id) ?: error("图片不存在")
        // 历史规则：status=0 时检查业务权限，status=2 只允许 owner_user_id 等于当前用户。
        // require((image.status == 0 && references.canRead(id)) ||
        //     (pending && image.status == 2 && image.ownerUserId == SecurityUtils.getUserId())) { "无权读取图片" }
        // 新增：确认事件可能等待 30 分钟；已实际关联的暂存图与列表缩略图保持相同业务授权。
        // 未关联的暂存图仍只对上传者开放，下载入口不开放暂存状态，待删除图始终拒绝。
        // 新增：先保留判定结果，拒绝时输出运行时状态，以定位手工查询与后台实际数据不一致。
        val readable = (image.status == 0 && references.canRead(id)) ||
            (pending && image.status == 2 &&
                (image.ownerUserId == SecurityUtils.getUserId() || references.canRead(id)))
        // 历史级别：log.warn；新增：当前文件日志仅接收 INFO/ERROR，拒绝信息以 INFO 持久化便于排查。
        if (!readable) log.info("imageReadDenied imageId={} status={} userId={} ownerUserId={} pendingPreview={} binding={}",
            id, image.status, SecurityUtils.getUserId(), image.ownerUserId, pending, image.bindingKey)
        require(readable) { "无权读取图片" }
        return image
    }
    // 历史签名选择：if (thumb) image.thumbnailPath ?: image.imagePath else image.imagePath。
    // 修正：列表缩略图签名不再回退原图，防止前端以小尺寸展示却下载完整图片。
    private fun signed(image: ImageStore, thumb: Boolean): String {
        val key = if (thumb) image.thumbnailPath?.takeIf { it.isNotBlank() && it != image.imagePath }
            else image.imagePath
        return client.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(storedBucket(image))
                .`object`(key ?: error(if (thumb) "缺少缩略图 key" else "缺少图片 key"))
                .expiry(15, TimeUnit.MINUTES).build())
    }

    /** 待后台确认但已经提交的图片也可按业务权限显示，避免短暂空白。 */
    override fun thumbnailUrls(ids: List<Long>): Map<Long, String> {
        require(ids.size <= 200) { "一次最多查询 200 张图片" }
        val urls = linkedMapOf<Long, String>()
        for (id in ids.distinct()) {
            val image = mapper.selectImageStoreById(id) ?: continue
            // 新增：缺少真实缩略图的历史记录不返回原图地址，前端按需提供原图查看入口。
            if (image.thumbnailPath.isNullOrBlank() || image.thumbnailPath == image.imagePath) continue
            if ((image.status == 0 || image.status == 2) && references.canRead(id)) urls[id] = signed(image, true)
        }
        return urls
    }

    /** 新增：只供已校验图片的业务写入使用，保持兼容列为稳定对象地址。 */
    @com.miracle.common.annotation.DataSource(com.miracle.common.enums.DataSourceType.SLAVE2)
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW, readOnly = true)
    override fun stableUrls(ids: List<Long>): List<String> = ids.map { id ->
        val image = mapper.selectImageStoreById(id) ?: error("图片不存在")
        "${config.endpoint.trimEnd('/')}/${storedBucket(image)}/${image.imagePath ?: error("缺少对象 key")}" 
    }

    /** 新增：跨库业务事务中查询图片使用独立 SQL Server 连接，避免被 MySQL 连接固定。 */
    @com.miracle.common.annotation.DataSource(com.miracle.common.enums.DataSourceType.SLAVE2)
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    override fun assertPendingOwned(imageId: Long, ownerUserId: Long) {
        val image = mapper.selectImageStoreById(imageId) ?: error("图片不存在")
        // 新增：有效期的最终校验由 reservePending 使用数据库 UTC 时钟完成。
        require(image.status == 2 && image.ownerUserId == ownerUserId) { "不是本人暂存图片" }
    }

    /** 新增：原子预留单业务归属；失败的业务提交仍由 24 小时过期扫描回收。 */
    @com.miracle.common.annotation.DataSource(com.miracle.common.enums.DataSourceType.SLAVE2)
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    override fun reservePending(imageId: Long, ownerUserId: Long, bindingKey: String) {
        // 新增：只允许该业务分桶上传的图片进入相应业务，避免绕过分桶规则。
        val image = mapper.selectImageStoreById(imageId) ?: error("图片不存在")
        // 历史分支：val prefix = if (bindingKey.startsWith("material:")) "material/" else "prod-order/"
        // 历史校验：require(image.imagePath?.startsWith(prefix) == true) { "图片业务类型不匹配" }
        references.validateBinding(image, bindingKey)
        require(mapper.reservePending(imageId, ownerUserId, bindingKey) == 1) { "图片已过期、不是本人上传或已被其他业务预留" }
    }

    // 历史实现保留：2026-10-14 改为业务事务与 outbox 驱动。
    //     override fun deleteImage(imageId: Long): Boolean {
    //         val record = mapper.selectImageStoreById(imageId)
    //             ?: throw IllegalArgumentException("图片不存在")
    //         val bucket = storedBucket(record)
    //         val key = record.imagePath ?: error("图片缺少对象 key")
    //         if (record.status == 0) {
    //             check(mapper.markImageDeleting(imageId) == 1) { "图片状态变更失败，请重试" }
    //         } else {
    //             check(record.status == 1) { "图片状态异常" }
    //         }
    //         client.removeObject(RemoveObjectArgs.builder().bucket(bucket).`object`(key).build())
    //         val deleted = mapper.deleteImageStoreById(imageId) == 1
    //         if (!deleted) log.error("对象已删除但元数据删除失败，待对账：bucket={} key={} imageId={}", bucket, key, imageId)
    //         return deleted
    //     }
    /** 新增：直接删除仅取消本人未绑定暂存图；已确认图片只能由业务 outbox 清理。 */
    override fun deleteImage(imageId: Long): Boolean {
        val image = mapper.selectImageStoreById(imageId) ?: return true
        require(image.status == 2 && image.ownerUserId == SecurityUtils.getUserId() && image.bindingKey == null) { "只能取消本人未提交暂存图" }
        require(!references.isReferenced(imageId)) { "图片仍有业务引用" }
        // 新增：在任何 MinIO 操作前通过条件更新排除并发业务预留。
        check(mapper.claimCancel(imageId, SecurityUtils.getUserId()) == 1) { "图片已被业务预留，请通过业务修改移除" }
        lifecycle.processDelete(imageId)
        return true
    }

    private fun storedBucket(record: ImageStore): String {
        val savedBucket: String? = record.bucketName
        if (!savedBucket.isNullOrBlank()) return savedBucket

        val legacyBucket: String? = config.legacyBucket
        if (!legacyBucket.isNullOrBlank()) return legacyBucket

        error("图片缺少 bucket，请先核实并回填历史记录")
    }

    override fun selectImageStoreList(imageStore: ImageStore): List<ImageStore> =
        // 新增：列表也按实际业务读取权限过滤。
        mapper.selectImageStoreList(imageStore).filter { it.imageId?.let(references::canRead) == true }

    override fun insertImageStore(imageStore: ImageStore): Int {
        // 历史实现：mapper.insertImageStore(imageStore)；生命周期元数据仅由上传生成。
        throw IllegalArgumentException("请使用图片上传接口")
    }

    // 历史实现保留：2026-10-14 改为业务事务与 outbox 驱动。
    //     override fun updateImageStore(imageStore: ImageStore): Int {
    //         imageStore.updateTime = Date()
    //         imageStore.updateBy = SecurityUtils.getLoginUser().user.nickName
    //         return mapper.updateImageStore(imageStore)
    //     }
    /** 新增：禁止任意修改 bucket/key/status，避免绕过生命周期状态机。 */
    override fun updateImageStore(imageStore: ImageStore): Int = throw IllegalArgumentException("不允许直接修改图片存储元数据")

    override fun deleteImageStoreByIds(imageIds: Array<Long>): Int =
        imageIds.count { deleteImage(it) }

    override fun selectImageStoreById(imageId: Long): ImageStore =
        mapper.selectImageStoreById(imageId)

    override fun selectImageStoreByPath(imagePath: String): ImageStore =
        mapper.selectImageStoreByPath(imagePath)
}
