package com.miracle.extra.service.impl
import com.miracle.common.annotation.DataSource
import com.miracle.common.enums.DataSourceType
import com.miracle.common.utils.SecurityUtils
import com.miracle.extra.domain.MesTransposeMaterial
import com.miracle.extra.mapper.MesTransposeMaterialMapper
import com.miracle.extra.service.IMesTransposeMaterialService
import com.miracle.extra.service.IImageStoreService
import com.miracle.extra.utils.PublicImageUrlResolver
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

/** 新增：物料事务固定 SQL Server SLAVE2，业务行与 outbox 同时提交，网络清理交给后台。 */
@Service @DataSource(DataSourceType.SLAVE2)
open class MesTransposeMaterialServiceImpl(private val mesTransposeMaterialMapper: MesTransposeMaterialMapper,
    private val imageStoreServiceImpl: IImageStoreService, private val materialOutbox: MaterialImageOutbox,
    private val publicImageUrls: PublicImageUrlResolver) : IMesTransposeMaterialService {
    override fun selectMesTransposeMaterialById(id: Long) = mesTransposeMaterialMapper.selectMesTransposeMaterialById(id)
    // 历史实现：override fun selectMesTransposeMaterialList(input: MesTransposeMaterial) = mesTransposeMaterialMapper.selectMesTransposeMaterialList(input)
    /** 新增：在一次列表查询结果上生成 public 缩略图 URL，保留分页对象与 total，不额外查图片库。 */
    override fun selectMesTransposeMaterialList(input: MesTransposeMaterial): List<MesTransposeMaterial> {
        val rows = mesTransposeMaterialMapper.selectMesTransposeMaterialList(input)
        for (row in rows) row.thumbnailUrl = publicImageUrls.thumbnailUrl(row.listImage)
        return rows
    }
    override fun selectMesTransposeMaterialListByMaterialCode(code: String) = mesTransposeMaterialMapper.selectMesTransposeMaterialListByMaterialCode(code)
    override fun selectMesTransposeMaterialListByMaterialName(name: String) = mesTransposeMaterialMapper.selectMesTransposeMaterialListByMaterialName(name)

    /** 新增：校验所有权及单业务归属，并维护无签名的历史兼容 URL。 */
    private fun reserve(input: MesTransposeMaterial) {
        input.materialImageId?.let {
            // 新增：以实际业务行 ID 预留，重复物料编码也不能共享同一暂存对象。
            imageStoreServiceImpl.reservePending(it, SecurityUtils.getUserId(), "material:${requireNotNull(input.id)}")
            input.materialImage = imageStoreServiceImpl.stableUrls(listOf(it)).first()
        }
    }
    @Transactional
    override fun insertMesTransposeMaterial(input: MesTransposeMaterial): Int {
        require(!input.materialCode.isNullOrBlank()) { "物料编码不能为空" }
        check(mesTransposeMaterialMapper.selectMesTransposeMaterialListByMaterialCode(input.materialCode!!).isEmpty()) { "物料编码已存在" }
        // 新增：先验证上传者，再插入取得物料主键；关联预留失败会回滚本库业务行。
        input.materialImageId?.let { imageStoreServiceImpl.assertPendingOwned(it, SecurityUtils.getUserId()) }
        if (input.materialImageId != null) input.materialImage = null
        input.createBy = SecurityUtils.getUsername(); input.createTime = Date()
        val rows = mesTransposeMaterialMapper.insertMesTransposeMaterial(input)
        check(rows == 1) { "物料新增失败" }
        if (input.materialImageId != null) {
            reserve(input)
            check(mesTransposeMaterialMapper.updateMesTransposeMaterial(input) == 1) { "物料图片地址更新失败" }
        }
        input.materialImageId?.let(materialOutbox::enqueueConfirm)
        return rows
    }
    /** 批量入口承担事务，同类内部调用沿用外层已开启的 SQL Server 事务。 */
    @Transactional
    override fun insertMesTransposeMaterialBatch(inputs: List<MesTransposeMaterial>): Int =
        inputs.sumOf { insertMesTransposeMaterial(it) }

    @Transactional
    override fun updateMesTransposeMaterial(input: MesTransposeMaterial): Int {
        val old = mesTransposeMaterialMapper.lockMaterial(requireNotNull(input.id)) ?: error("物料不存在")
        val changed = old.materialImageId != input.materialImageId
        if (changed) reserve(input)
        else input.materialImage = old.materialImage // 原 ID 不变时不接受客户端伪造 URL。
        input.updateBy = SecurityUtils.getUsername(); input.updateTime = Date()
        val rows = mesTransposeMaterialMapper.updateMesTransposeMaterial(input)
        check(rows == 1) { "物料修改失败" }
        if (changed) {
            input.materialImageId?.let(materialOutbox::enqueueConfirm)
            old.materialImageId?.let(materialOutbox::enqueueDeleteIfUnused)
        }
        return rows
    }
    @Transactional
    override fun deleteMesTransposeMaterialById(id: Long): Int {
        val old = mesTransposeMaterialMapper.lockMaterial(id) ?: error("物料不存在")
        check(mesTransposeMaterialMapper.deleteMesTransposeMaterialById(id) == 1)
        old.materialImageId?.let(materialOutbox::enqueueDeleteIfUnused)
        return 1
    }
    @Transactional
    override fun deleteMesTransposeMaterialByIds(ids: Array<Long>): Int =
        ids.distinct().sumOf { deleteMesTransposeMaterialById(it) }
}

// 历史完整实现保留（2026-10-14）：
// package com.miracle.extra.service.impl
// 
// import com.miracle.common.exception.ServiceException
// import com.miracle.common.utils.SecurityUtils
// import org.springframework.stereotype.Service
// import com.miracle.extra.service.IMesTransposeMaterialService
// import com.miracle.extra.domain.MesTransposeMaterial
// import com.miracle.extra.mapper.MesTransposeMaterialMapper
// import com.miracle.extra.utils.SnowflakeIdGenerator
// import java.util.Date
// 
// @Service
// class MesTransposeMaterialServiceImpl(
//     private val mesTransposeMaterialMapper: MesTransposeMaterialMapper,
//     private val imageStoreServiceImpl: ImageStoreServiceImpl)
//     :IMesTransposeMaterialService {
// 
//     override fun selectMesTransposeMaterialById(id: Long): MesTransposeMaterial {
//         return mesTransposeMaterialMapper.selectMesTransposeMaterialById(id)
//     }
// 
//     override fun selectMesTransposeMaterialList(mesTransposeMaterial: MesTransposeMaterial): List<MesTransposeMaterial> =
//         mesTransposeMaterialMapper.selectMesTransposeMaterialList(mesTransposeMaterial)
// 
//     override fun selectMesTransposeMaterialListByMaterialCode(materialCode: String): List<MesTransposeMaterial> =
//         mesTransposeMaterialMapper.selectMesTransposeMaterialListByMaterialCode(materialCode)
// 
//     override fun selectMesTransposeMaterialListByMaterialName(materialName: String): List<MesTransposeMaterial> =
//         mesTransposeMaterialMapper.selectMesTransposeMaterialListByMaterialName(materialName)
// 
//     override fun insertMesTransposeMaterial(mesTransposeMaterial: MesTransposeMaterial): Int {
//         val code = mesTransposeMaterial.materialCode
//         val createBy : String = SecurityUtils.getLoginUser().user.nickName
//         val createTime : Date = Date()
//         if(code.isNullOrEmpty()) throw ServiceException("物料编码不能为空！")
//         if(mesTransposeMaterialMapper.selectMesTransposeMaterialListByMaterialCode(code).isNotEmpty()) throw ServiceException("该物料编码已存在！")
//         mesTransposeMaterial.apply {
//             this.createBy = createBy
//             this.createTime = createTime
//         }
//         return mesTransposeMaterialMapper.insertMesTransposeMaterial(mesTransposeMaterial)
//     }
// 
//     override fun insertMesTransposeMaterialBatch(mesTransposeMaterialList: List<MesTransposeMaterial>): Int =
//         mesTransposeMaterialMapper.insertMesTransposeMaterialBatch(mesTransposeMaterialList)
// 
// 
//     override fun updateMesTransposeMaterial(mesTransposeMaterial: MesTransposeMaterial): Int {
//         val materialCode = mesTransposeMaterial.materialCode
//         val originImageId = mesTransposeMaterialMapper.selectMesTransposeMaterialListByMaterialCode(materialCode.toString()).firstOrNull()?.materialImageId
//         val originImageUrl = mesTransposeMaterialMapper.selectMesTransposeMaterialListByMaterialCode(materialCode.toString()).firstOrNull()?.materialImage
//         val currentImageId = mesTransposeMaterial.materialImageId
//         val currentImageUrl = mesTransposeMaterial.materialImage
//         if(currentImageId != originImageId && originImageId != null){
//             imageStoreServiceImpl.deleteImage(originImageId)
//         }
//         val updateBy : String = SecurityUtils.getLoginUser().user.nickName
//         val updateTime : Date = Date()
//         mesTransposeMaterial.apply {
//             this.updateBy = updateBy
//             this.updateTime = updateTime
//             this.materialImage = currentImageUrl
//             this.materialImageId = currentImageId
//         }
//         return mesTransposeMaterialMapper.updateMesTransposeMaterial(mesTransposeMaterial)
//     }
// 
//     override fun deleteMesTransposeMaterialById(id: Long): Int =
//         mesTransposeMaterialMapper.deleteMesTransposeMaterialById(id)
// 
//     override fun deleteMesTransposeMaterialByIds(ids: Array<Long>): Int =
//         mesTransposeMaterialMapper.deleteMesTransposeMaterialByIds(ids)
// 
//     private fun basicGenerateId(snowflakeIdGenerator: SnowflakeIdGenerator) : Long =
//         snowflakeIdGenerator.nextId();
// }
