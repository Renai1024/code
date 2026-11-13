package com.miracle.extra.domain;

import com.miracle.common.annotation.Excel
import com.miracle.common.core.domain.BaseEntity


/**
 * 转序物料信息 mes_transpose_material
 *
 * @author miracle
 * @date 2026-01-20
 */

data class MesTransposeMaterial(
    var id: Long? = null,

    @Excel(name = "物料编码")
    var materialCode: String? = null,

    @Excel(name = "物料名称")
    var materialName: String? = null,

    @Excel(name = "生产车间")
    var materialWorkshop: String? = null,

    var materialImage: String? = null,

    var materialImageId: Long? = null,

    /** 新增：public 桶缩略图固定访问地址，仅随列表响应返回，不写业务表、不接受客户端赋值。 */
    @get:com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    var thumbnailUrl: String? = null,

    /** 新增：列表 LEFT JOIN 一次取出的存储定位信息，内部生成 URL 使用，不暴露给前端。 */
    @get:com.fasterxml.jackson.annotation.JsonIgnore
    var listImage: ImageStore? = null,
) : BaseEntity()



