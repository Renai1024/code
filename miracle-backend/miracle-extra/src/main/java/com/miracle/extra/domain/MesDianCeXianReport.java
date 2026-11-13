package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MesDianCeXianReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 日期（MCGS_Date） */
    @Excel(name = "日期")
    private String mcgsDate;

    /** 时间（MCGS_Time） */
    @Excel(name = "时间")
    private String mcgsTime;

    /** 条码 */
    @Excel(name = "条码")
    private String barcode;

    /** 设备编号 */
    @Excel(name = "设备编号")
    private String equipmentNo;

    /** 批次号 */
    @Excel(name = "批次号")
    private String batchNumber;

    /** 绝缘1 */
    @Excel(name = "绝缘1")
    private String insulation1;

    /** 绝缘2 */
    @Excel(name = "绝缘2")
    private String insulation2;

    /** 绝缘3 */
    @Excel(name = "绝缘3")
    private String insulation3;

    /** 绝缘4 */
    @Excel(name = "绝缘4")
    private String insulation4;

    /** 耐压1 */
    @Excel(name = "耐压1")
    private String voltageResistance1;

    /** 耐压2 */
    @Excel(name = "耐压2")
    private String voltageResistance2;

    /** 绝缘1合格 */
    @Excel(name = "绝缘1合格")
    private String insulation1Qualified;

    /** 绝缘2合格 */
    @Excel(name = "绝缘2合格")
    private String insulation2Qualified;

    /** 绝缘3合格 */
    @Excel(name = "绝缘3合格")
    private String insulation3Qualified;

    /** 绝缘4合格 */
    @Excel(name = "绝缘4合格")
    private String insulation4Qualified;

    /** 耐压1合格 */
    @Excel(name = "耐压1合格")
    private String voltageResistance1Qualified;

    /** 耐压2合格 */
    @Excel(name = "耐压2合格")
    private String voltageResistance2Qualified;

    /** 导通1合格 */
    @Excel(name = "导通1合格")
    private String connect1Qualified;

    /** 导通2合格 */
    @Excel(name = "导通2合格")
    private String connect2Qualified;

    /** 导通3合格 */
    @Excel(name = "导通3合格")
    private String connect3Qualified;

    /** 导通4合格 */
    @Excel(name = "导通4合格")
    private String connect4Qualified;

    /** 主电容容量 */
    @Excel(name = "主电容容量")
    private String interpolarCapability;

    /** 主电容损耗 */
    @Excel(name = "主电容损耗")
    private String interpolarLoss;

    /** 接地电容容量 */
    @Excel(name = "接地电容容量")
    private String polarRegionCapability;

    /** 极地电容损耗 */
    @Excel(name = "极地电容损耗")
    private String polarRegionLoss;

    /** 主电容容损合格 */
    @Excel(name = "主电容容损合格")
    private String interpolarQualified;

    /** 接地电容容损合格 */
    @Excel(name = "接地电容容损合格")
    private String polarRegionQualified;

    /** 绝缘5*/
    @Excel(name = "绝缘5")
    private String insulation5;

    /** 绝缘6*/
    @Excel(name = "绝缘6")
    private String insulation6;

    /** 耐压3*/
    @Excel(name = "耐压3")
    private String voltageResistance3;

    /** 耐压5*/
    @Excel(name = "耐压5")
    private String voltageResistance5;

    /** 极地绝缘*/
    @Excel(name = "极地绝缘")
    private String polarRegionInsulatio;

    /** 绝缘5合格*/
    @Excel(name = "绝缘5合格")
    private String Insulatio5Qualified;

    /** 绝缘6合格*/
    @Excel(name = "绝缘6合格")
    private String Insulatio6Qualified;

    /** 耐压3合格*/
    @Excel(name =  "耐压3合格")
    private String voltageResistance3Qualified;

    /** 耐压5合格*/
    @Excel(name =  "耐压5合格")
    private String voltageResistance5Qualified;

    /** 极地绝缘判合格*/
    @Excel(name =  "极地绝缘合格")
    private String polarRegionInsulatioQualified;

    /** 总体判定*/
    @Excel(name =  "总体判定")
    private String overaJudgment;

    /** 测试时间（合并显示字段） */
    @Excel(name = "测试时间", width = 30)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String testDateTime;

    // ========== 查询条件字段（非数据库字段）==========

    /** 测试开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date testStartTime;

    /** 测试结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date testEndTime;
}