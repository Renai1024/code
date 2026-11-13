package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MesSheBeiZuDataReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 所属类型 */
    @Excel(name = "所属类型")
    private String customerFlag;

    /** 生产订单号 */
    @Excel(name = "生产订单号")
    private String materialCode;

    /** 测试开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "测试开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date checkStartTime;

    /** 测试结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "测试结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date checkEndTime;

    /** 测试原始结果 */
    @Excel(name = "测试原始结果")
    private String resultStr;

    /** 测试解析结果 */
    @Excel(name = "测试解析结果")
    private String escapeStr;

    /** 条码 */
    @Excel(name = "条码")
    private String barCode;

    /** 产品料号 */
    @Excel(name = "产品料号")
    private String myField1;

    /** 旧料号 */
    @Excel(name = "旧料号")
    private String myField2;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String myField3;

    /** 操作人 */
    @Excel(name = "操作人")
    private String myField4;

    /** 自定义字段5 */
    @Excel(name = "自定义字段5")
    private String myField5;

    /** 自定义字段6 */
    @Excel(name = "自定义字段6")
    private String myField6;

    // ========== 查询条件字段（非数据库字段）==========
    /** 测试开始时间-起始 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTimeStart;

    /** 测试开始时间-结束 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTimeEnd;

    /** 条码查询 */
    private String barCodeQuery;

    /** 生产订单号查询 */
    private String materialCodeQuery;

    /** 产品料号查询（MyField1） */
    private String productCodeQuery;

    /** 操作人查询（MyField4） */
    private String operatorQuery;
}
