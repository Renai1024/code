package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 线束车间外观检验条秒录入
 */
@Data
public class MesXSCJLabelCheck implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 **/
    private String fid;

    /** 条码 **/
    @Excel(name = "条码")
    private String barCodeStr;

    /** 扫码日期 **/
    @Excel(name = "扫码日期")
    private Date scanDate;

    /** 设备名称 **/
    @Excel(name = "设备名称")
    private String scanMachine;

    /** 设备IP **/
    @Excel(name = "设备IP")
    private String scanIPAddress;

    /** 所属类型 **/
    @Excel(name = "所属类型")
    private String customerFlag;

    @Excel(name = "所属类型")
    /** 操作人 **/
    private String operator;

    @Excel(name = "产品料号")
    /** 产品料号 **/
    private String materialCode;

    @Excel(name = "是否重码")
    /** 是否重码 **/
    private Boolean duplicateFlag;

    // ========== 查询条件字段（非数据库字段）==========

    /** 测试开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date testStartTime;

    /** 测试结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date testEndTime;
}
