package com.miracle.extra.service.impl

import com.miracle.extra.domain.ImageStore
import com.miracle.extra.mapper.ImageStoreMapper
import com.miracle.common.utils.SecurityUtils
import com.miracle.common.core.domain.model.LoginUser
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

/** 新增：按持久化对象路径与预留归属路由引用检查，不以模块全局开关限制后续业务。 */
@Service
class ImageReferenceService(private val images: ImageStoreMapper,
                            private val businesses: List<ImageBusinessReference>) {
    // 新增：仅在拒绝读取时记录判定依据，不打印签名 URL、令牌或存储凭据。
    private val log = LoggerFactory.getLogger(javaClass)
    init {
        // 新增：拒绝重复或互相覆盖的前缀，避免新增业务后路由结果受 Bean 注册顺序影响。
        require(businesses.isNotEmpty()) { "未注册图片业务引用检查器" }
        for (a in businesses.indices) for (b in a + 1 until businesses.size) {
            val left = businesses[a]; val right = businesses[b]
            require(!left.pathPrefix.startsWith(right.pathPrefix) && !right.pathPrefix.startsWith(left.pathPrefix) &&
                !left.bindingPrefix.startsWith(right.bindingPrefix) && !right.bindingPrefix.startsWith(left.bindingPrefix)) {
                "图片业务前缀冲突"
            }
        }
        require(businesses.all { it.pathPrefix.isNotBlank() && it.pathPrefix.endsWith('/') &&
            it.bindingPrefix.isNotBlank() && it.bindingPrefix.endsWith(':') && it.readPermission.isNotBlank() }) {
            "图片业务检查器配置不完整"
        }
    }

    /** 新增：新图片按实际路径定位；旧图片归属不明确时保守检查全部已注册业务，失败不删。 */
    private fun handlers(image: ImageStore): List<ImageBusinessReference> {
        val byPath = businesses.singleOrNull { image.imagePath?.startsWith(it.pathPrefix) == true }
        val binding = image.bindingKey?.takeIf { it.isNotBlank() }
        if (binding != null) {
            val byBinding = businesses.singleOrNull { binding.startsWith(it.bindingPrefix) }
            check(byBinding != null && byBinding === byPath) { "图片路径与业务归属不匹配，禁止清理" }
        }
        // 新增：迁移前无归属/上传者元数据的旧图片可能曾跨模块复用 URL，不能仅凭目录缩小检查范围。
        if (binding == null && image.ownerUserId == null) return businesses
        return byPath?.let { listOf(it) } ?: businesses
    }

    /** 新增：预留只允许业务检查器对应的对象目录，新增业务不再落入默认工单分支。 */
    fun validateBinding(image: ImageStore, bindingKey: String) {
        val handler = businesses.singleOrNull { bindingKey.startsWith(it.bindingPrefix) }
        require(handler != null && image.imagePath?.startsWith(handler.pathPrefix) == true) { "图片业务类型不匹配或未注册" }
    }

    fun isReferenced(id: Long): Boolean {
        val image = images.selectImageStoreById(id) ?: return false
        return handlers(image).any { it.countReferences(image) > 0 }
    }

    /** 新增：过期清理只检查图片所属业务的未完成确认，旧归属不明图片保守检查全部业务。 */
    fun hasPendingConfirmation(id: Long): Boolean {
        val image = images.selectImageStoreById(id) ?: return false
        return handlers(image).any { it.hasPendingConfirmation(id) }
    }

    /** 新增：读取权限与业务路由保持一致，不查询无关业务表。 */
    fun canRead(id: Long): Boolean {
        // 历史实现保留：
        // val image = images.selectImageStoreById(id) ?: return false
        // return handlers(image).any { it.countReferences(image) > 0 &&
        //     (SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasPermi(it.readPermission)) }
        val image = images.selectImageStoreById(id)
        if (image == null) {
            // 历史日志：log.warn("imageBusinessReadDenied imageId={} reason=metadataMissing", id)
            // 新增：现有 logback 的文件过滤器仅保存 INFO/ERROR，使用 INFO 保证诊断落入 sys-info.log。
            log.info("imageBusinessReadDenied imageId={} reason=metadataMissing", id)
            return false
        }
        // 新增：没有登录主体时按拒绝处理，诊断本身不因获取用户异常掩盖原来的拒绝原因。
        val userId = (SecurityUtils.getAuthentication()?.principal as? LoginUser)?.userId
        val admin = SecurityUtils.isAdmin(userId)
        val checks = mutableListOf<String>()
        for (business in handlers(image)) {
            val count = business.countReferences(image)
            val permitted = admin || (userId != null && SecurityUtils.hasPermi(business.readPermission))
            if (count > 0 && permitted) return true
            checks.add("${business.pathPrefix}:references=$count,permission=${business.readPermission},granted=$permitted")
        }
        // 历史级别：log.warn；新增：拒绝详情写入现有 INFO 文件，避免只在控制台瞬时可见。
        log.info("imageBusinessReadDenied imageId={} status={} userId={} superAdmin={} binding={} checks={}",
            id, image.status, userId, admin, image.bindingKey, checks.joinToString(";"))
        return false
    }
}

// 历史实现完整保留：无条件跨业务查询及随后增加的仅物料开关，现由业务检查器替代。
// package com.miracle.extra.service.impl
// import com.miracle.extra.mapper.MesTransposeMaterialMapper
// import com.miracle.extra.mapper.ProdOrderImageMapper
// import com.miracle.extra.mapper.ImageStoreMapper
// import com.miracle.common.utils.SecurityUtils
// import org.springframework.stereotype.Service
// import com.miracle.extra.config.MinioConfig
// 
// /** 新增：跨库引用查询在无业务事务的工作线程执行，任一库故障均阻止删除。 */
// @Service
// class ImageReferenceService(private val materials: MesTransposeMaterialMapper, private val orders: ProdOrderImageMapper,
//                             private val images: ImageStoreMapper, private val config: MinioConfig) {
//     /** 历史工单没有 ID 关联时也查询旧 URL，避免过渡期误删仍在使用的历史对象。 */
//     private fun legacyCount(id: Long): Int {
//         // 新增：仅物料模式不访问任何工单业务表，不能依靠吞掉 SQL 异常判断无引用。
//         if (!config.orderImagesEnabled) return 0
//         val url = images.selectImageStoreById(id)?.imageUrl?.takeIf { it.isNotBlank() } ?: return 0
//         return orders.countLegacyImageReferences(url)
//     }
//     fun isReferenced(id: Long): Boolean {
//         val materialCount = materials.countImageReferences(id)
//         // 历史实现：val orderCount = orders.countImageReferences(id)
//         val orderCount = if (config.orderImagesEnabled) orders.countImageReferences(id) else 0
//         val legacyOrderCount = legacyCount(id)
//         return materialCount > 0 || orderCount > 0 || legacyOrderCount > 0
//     }
//     /** 当前模块没有行级数据范围：先验证图片实际关联，再应用对应模块现有菜单权限。 */
//     fun canRead(id: Long): Boolean {
//         return (materials.countImageReferences(id) > 0 &&
//             (SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasPermi("extra:mes_transpose_material:list"))) ||
//             // 历史条件：(orders.countImageReferences(id) > 0 || legacyCount(id) > 0)。
//             (config.orderImagesEnabled && (orders.countImageReferences(id) > 0 || legacyCount(id) > 0) &&
//             (SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasPermi("extra:mes_picture_upload:list")))
//     }
// }
