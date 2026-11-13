package com.miracle.extra.service.impl

import com.miracle.extra.domain.ImageStore
import com.miracle.extra.mapper.MesTransposeMaterialMapper
import com.miracle.extra.mapper.ProdOrderImageMapper
import org.springframework.stereotype.Service

/** 新增：业务图片引用扩展点；新增业务实现并注册 Spring Bean，共享清理流程无需追加业务分支。 */
interface ImageBusinessReference {
    val pathPrefix: String
    val bindingPrefix: String
    val readPermission: String
    fun countReferences(image: ImageStore): Int
    fun hasPendingConfirmation(imageId: Long): Boolean
}

/** 新增：物料图片只查询物料业务库，不依赖工单业务表。 */
@Service
class MaterialImageReference(private val materials: MesTransposeMaterialMapper,
                             private val outbox: MaterialImageOutbox) : ImageBusinessReference {
    override val pathPrefix = "material/"
    override val bindingPrefix = "material:"
    override val readPermission = "extra:mes_transpose_material:list"
    override fun countReferences(image: ImageStore) = materials.countImageReferences(requireNotNull(image.imageId))
    override fun hasPendingConfirmation(imageId: Long) = outbox.pendingConfirm(imageId)
}

/** 新增：工单图片保留 ID 关联和历史 URL 引用检查，相关查询异常继续阻止物理删除。 */
@Service
class OrderImageReference(private val orders: ProdOrderImageMapper,
                          private val outbox: OrderImageOutbox) : ImageBusinessReference {
    override val pathPrefix = "prod-order/"
    override val bindingPrefix = "order:"
    override val readPermission = "extra:mes_picture_upload:list"
    override fun countReferences(image: ImageStore): Int {
        val byId = orders.countImageReferences(requireNotNull(image.imageId))
        val byUrl = image.imageUrl?.takeIf { it.isNotBlank() }?.let(orders::countLegacyImageReferences) ?: 0
        return byId + byUrl
    }
    override fun hasPendingConfirmation(imageId: Long) = outbox.pendingConfirm(imageId)
}
