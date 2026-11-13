package com.miracle.extra.mapper

import com.miracle.common.annotation.DataSource
import com.miracle.common.enums.DataSourceType
import com.miracle.extra.domain.MesTransposeMaterial
import org.apache.ibatis.annotations.Mapper

@Mapper
@DataSource(DataSourceType.SLAVE2)
interface MesTransposeMaterialMapper {
    /** 新增：删除前必须查询真实业务引用；异常向上传播，不视作零引用。 */
    @org.apache.ibatis.annotations.Select("select count(1) from mes_transpose_material where material_image_id = #{imageId}")
    fun countImageReferences(@org.apache.ibatis.annotations.Param("imageId") imageId: Long): Int


    /** 新增：同一物料的并发修改串行执行，旧图片以当前锁定行确定。 */
    @org.apache.ibatis.annotations.Select("select * from mes_transpose_material with (UPDLOCK, ROWLOCK) where id = #{id}")
    @org.apache.ibatis.annotations.ResultMap("MesTransposeMaterialResult")
    fun lockMaterial(@org.apache.ibatis.annotations.Param("id") id: Long): MesTransposeMaterial?

    fun selectMesTransposeMaterialById(id: Long?): MesTransposeMaterial

    fun selectMesTransposeMaterialList(mesTransposeMaterial: MesTransposeMaterial): List<MesTransposeMaterial>

    fun selectMesTransposeMaterialListByMaterialCode(materialCode: String): List<MesTransposeMaterial>

    fun selectMesTransposeMaterialListByMaterialName(materialName: String): List<MesTransposeMaterial>

    fun insertMesTransposeMaterial(mesTransposeMaterial: MesTransposeMaterial): Int

    fun insertMesTransposeMaterialBatch(mesTransposeMaterialList: List<MesTransposeMaterial>): Int

    fun updateMesTransposeMaterial(mesTransposeMaterial: MesTransposeMaterial): Int

    fun deleteMesTransposeMaterialById(id: Long): Int

    fun deleteMesTransposeMaterialByIds(ids: Array<Long>): Int
}