package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MesBreakoverCheckTH9110Report implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 所属类型 */
    @Excel(name = "所属类型")
    private String customerFlag;

    /** 产品料号 */
    @Excel(name = "产品料号")
    private String materialCode;

    /** 客户料号 */
    @Excel(name = "客户料号")
    private String custMaterialCode;

    /** 旧料号 */
    @Excel(name = "旧料号")
    private String oldMaterialCode;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String materialName;

    /** 生产订单 */
    @Excel(name = "生产订单")
    private String productionOrder;

    /** 测试时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "测试时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

    /** 条码 */
    @Excel(name = "条码")
    private String barCode;

    /** 段结果字符串 */
    @Excel(name = "段结果字符串")
    private String sectionResultStr;

    /** 测试步骤 */
    @Excel(name = "测试步骤")
    private String testStep;

    /** 测试项目 */
    @Excel(name = "测试项目")
    private String testProject;

    /** 测试电压 */
    @Excel(name = "测试电压")
    private Double testVoltage;

    /** 测试电流 */
    @Excel(name = "测试电流")
    private Double testElectricity;

    /** 判定结果 */
    @Excel(name = "判定结果")
    private String judgmentResult;

    /** 总体结果 */
    @Excel(name = "总体结果")
    private String overallResult;

    /** 操作员 */
    @Excel(name = "操作员")
    private String operator;

    /** 单码 */
    private String singleCode;

    /** 原始结果字符串 */
    private String originalResultStr;

    /** 序号 */
    private Integer sn;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // ========== 查询条件字段（非数据库字段）==========
    /** 测试开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkStartTime;

    /** 测试结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkEndTime;

    /** 总体结果筛选 */
    private String overallResultFilter;
}
