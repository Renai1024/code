package com.miracle.extra.service.impl
import com.miracle.extra.mapper.ImageStoreMapper
import com.miracle.extra.domain.ImageEvent
import com.miracle.extra.config.MinioConfig
import io.minio.MinioClient
import io.minio.RemoveObjectArgs
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.annotation.EnableScheduling

/** 新增：工作线程不持有业务事务；跨库查询失败时保留对象和事件，后续退避重试。 */
@Service
@EnableScheduling
class ImageLifecycleWorker(private val mapper: ImageStoreMapper, private val client: MinioClient,
                           private val references: ImageReferenceService, private val config: MinioConfig,
                           private val material: MaterialImageOutbox, private val order: OrderImageOutbox) {
    private val log = LoggerFactory.getLogger(javaClass)
    fun process(event: ImageEvent) {
        when (event.type) {
            "CONFIRM" -> {
                // 引用已被后续业务删除时，早期确认事件可完成，删除事件/过期扫描继续负责清理。
                if (!references.isReferenced(event.imageId)) return
                val image = mapper.selectImageStoreById(event.imageId) ?: error("待确认图片不存在")
                if (image.status == 0) return
                check(image.status == 2) { "图片状态不可确认" }
                if (mapper.confirmPending(event.imageId) != 1) {
                    check(mapper.selectImageStoreById(event.imageId)?.status == 0)
                }
            }
            "DELETE_IF_UNUSED" -> processDelete(event.imageId)
            else -> error("未知图片事件：${event.type}")
        }
    }
    /** 先保留待删除元数据再操作两份对象，任一失败可幂等重试。 */
    fun processDelete(id: Long) {
        if (references.isReferenced(id)) return
        val image = mapper.selectImageStoreById(id) ?: return
        val bucket = image.bucketName?.takeIf { it.isNotBlank() }
            ?: config.legacyBucket?.takeIf { it.isNotBlank() } ?: error("请回填历史图片 bucket")
        val key = image.imagePath ?: error("缺少原图 key")
        if (image.status != 1) check(mapper.markImageDeleting(id) == 1)
        image.thumbnailPath?.let { client.removeObject(RemoveObjectArgs.builder().bucket(bucket).`object`(it).build()) }
        client.removeObject(RemoveObjectArgs.builder().bucket(bucket).`object`(key).build())
        // 新增：删除对象后元数据失败仍抛出异常，outbox 保留重试；并发重放已删记录视为成功。
        check(mapper.deleteImageStoreById(id) == 1 || mapper.selectImageStoreById(id) == null) { "图片元数据清理失败" }
    }
    /** 每个库独立容错，单库故障不阻断另一个库的事件认领。 */
    // 历史调度保留：@Scheduled(fixedDelay = 5000)
    // 新增：上一轮处理完成后等待 30 分钟，再扫描两库确认/删除事件及恢复过期认领。
    @Scheduled(fixedDelay = 30 * 60 * 1000L)
    fun processOutbox() {
        for (kind in 0..1) {
            // 历史说明：仅物料开关曾用于跳过工单事件，现已停用。
            // 历史仅物料开关：if (kind == 1 && !config.orderImagesEnabled) continue。
            // 新增：各库 outbox 独立处理，业务表检查交给图片所属业务的处理器。
            try {
                if (kind == 0) material.recoverExpiredClaims() else order.recoverExpiredClaims()
                for (i in 0 until 100) {
                    val event = (if (kind == 0) material.claimNext() else order.claimNext()) ?: break
                    try {
                        process(event)
                        if (kind == 0) material.complete(event) else order.complete(event)
                    } catch (failure: Exception) {
                        log.error("图片事件处理失败 eventId={}", event.eventId, failure)
                        if (kind == 0) material.retry(event, failure.message ?: "unknown")
                        else order.retry(event, failure.message ?: "unknown")
                    }
                }
            } catch (failure: Exception) { log.error("图片 outbox 仓储失败 kind={}", kind, failure) }
        }
    }
    /** 过期扫描先排除引用和未完成确认，再条件认领；待删除状态提供上传补偿兜底。 */
    @Scheduled(fixedDelay = 30 * 60 * 1000L)
    fun cleanExpired() {
        try {
            for (image in mapper.expiredPending()) {
                val id = image.imageId ?: continue
                try {
                    // 历史条件：references.isReferenced(id) || material.pendingConfirm(id) || order.pendingConfirm(id)。
                    // 新增：按图片归属选择业务检查器；历史归属不明时保守检查。
                    // 历史仅物料条件：references.isReferenced(id) || material.pendingConfirm(id) || (config.orderImagesEnabled && order.pendingConfirm(id))。
                    if (references.isReferenced(id) || references.hasPendingConfirmation(id)) continue
                    if (mapper.claimExpired(id) == 1) processDelete(id)
                } catch (failure: Exception) { log.error("过期图片清理失败 id={}", id, failure) }
            }
            for (image in mapper.deletingImages()) {
                try { processDelete(image.imageId!!) }
                catch (failure: Exception) { log.error("待删除图片重试失败 id={}", image.imageId, failure) }
            }
        } catch (failure: Exception) { log.error("图片清理扫描失败", failure) }
    }
}
