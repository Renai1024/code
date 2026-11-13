package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MesDengLiZiQingXiReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 条码1 */
    @Excel(name = "条码1")
    private String sn1;

    /** 条码2 */
    @Excel(name = "条码2")
    private String sn2;

    /** R轴速度 */
    @Excel(name = "R轴速度")
    private String rs;

    /** 左Y轴速度 */
    @Excel(name = "左Y轴速度")
    private String lys;

    /** 右Y轴速度 */
    @Excel(name = "右Y轴速度")
    private String rys;

    /** X轴速度 */
    @Excel(name = "X轴速度")
    private String xs;

    /** Z轴速度 */
    @Excel(name = "Z轴速度")
    private String zs;

    /** 高度 */
    @Excel(name = "高度")
    private String height;

    /** 角度 */
    @Excel(name = "角度")
    private String angle;

    /** 档位 */
    @Excel(name = "档位")
    private String tap;

    /** 时间 */
    @Excel(name = "时间")
    private String tTime;

    // ========== 查询条件字段（非数据库字段）==========
    /** 创建开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createStartTime;

    /** 创建结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createEndTime;
}