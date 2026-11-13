package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * MES外箱码绑定记录
 */
@Data
public class MesOuterBoxBindRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键UUID **/
    private String uuid;

    /** 外箱编码 **/
    @Excel(name = "外箱编码")
    private String outerBoxBarcode;

    /** 产品编码 **/
    @Excel(name = "产品编码")
    private String productBarcode;

    /** 绑定顺序 **/
    @Excel(name = "绑定顺序")
    private short bindSn;

    /** 绑定时间 **/
    @Excel(name = "绑定时间")
    private Date bindTime;

    /** 绑定人 **/
    @Excel(name = "绑定人")
    private String bindBy;

    // ========== 查询条件字段（非数据库字段）==========
    /** 绑定开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bindStartTime;

    /** 绑定结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bindEndTime;

    @Data
    /** 箱码绑定请求 */
    public static class BindRequest {
        private String[] productBarcodes;
        private String outerBoxBarcode;
    }
}
