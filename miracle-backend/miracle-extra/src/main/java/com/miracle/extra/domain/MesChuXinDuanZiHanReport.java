package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MesChuXinDuanZiHanReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 记录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "记录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 批次号 */
    @Excel(name = "批次号")
    private String batchNo;

    /** 生产时间 */
    @Excel(name = "生产时间")
    private String productionTime;

    /** 产品代码 */
    @Excel(name = "产品代码")
    private String productCode;

    /** 焊接时间 */
    @Excel(name = "焊接时间")
    private Double weldingTime;

    /** 焊接振幅 */
    @Excel(name = "焊接振幅")
    private Double weldingAmplitude;

    /** 焊接压力 */
    @Excel(name = "焊接压力")
    private Double weldingPressure;

    /** 焊接能量 */
    @Excel(name = "焊接能量")
    private Double weldingEnergy;

    /** 焊接功率 */
    @Excel(name = "焊接功率")
    private Double weldingPower;

    /** 预压高度 */
    @Excel(name = "预压高度")
    private Double preloadingHeight;

    /** 焊后高度 */
    @Excel(name = "焊后高度")
    private Double postWeldingHeight;

    /** 焊接深度 */
    @Excel(name = "焊接深度")
    private Double weldingDepth;

    /** 操作模式 */
    @Excel(name = "操作模式")
    private String operationMode;

    /** 工作模式 */
    @Excel(name = "工作模式")
    private String workingMode;

    /** 时间上限 */
    @Excel(name = "时间上限")
    private Double timeUpperLimit;

    /** 时间下限 */
    @Excel(name = "时间下限")
    private Double timeLowerLimit;

    /** 功率上限 */
    @Excel(name = "功率上限")
    private Double powerUpperLimit;

    /** 功率下限 */
    @Excel(name = "功率下限")
    private Double powerLowerLimit;

    /** 能量上限 */
    @Excel(name = "能量上限")
    private Double energyUpperLimit;

    /** 能量下限 */
    @Excel(name = "能量下限")
    private Double energyLowerLimit;

    /** 预压上限 */
    @Excel(name = "预压上限")
    private Double preloadingUpperLimit;

    /** 预压下限 */
    @Excel(name = "预压下限")
    private Double preloadingLowerLimit;

    /** 焊后上限 */
    @Excel(name = "焊后上限")
    private Double postWeldingUpperLimit;

    /** 焊后下限 */
    @Excel(name = "焊后下限")
    private Double postWeldingLowerLimit;

    /** 焊头代码 */
    @Excel(name = "焊头代码")
    private String hornCode;

    /** 砧座代码 */
    @Excel(name = "砧座代码")
    private String anvilCode;

    /** 夹具代码 */
    @Excel(name = "夹具代码")
    private String holderCode;

    /** 焊头A使用次数 */
    @Excel(name = "焊头A使用次数")
    private Double hornAUseCount;

    /** 焊头B使用次数 */
    @Excel(name = "焊头B使用次数")
    private Double hornBUseCount;

    /** 砧座使用次数 */
    @Excel(name = "砧座使用次数")
    private Double anvilUseCount;

    /** 夹具使用次数 */
    @Excel(name = "夹具使用次数")
    private Double holderUseCount;

    /** 报警状态 */
    @Excel(name = "报警状态")
    private String alarmStatus;

    /** 设备编号 */
    @Excel(name = "设备编号")
    private String equipmentNumber;

    // ========== 查询条件字段（非数据库字段）==========
    /** 创建开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createStartTime;

    /** 创建结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createEndTime;
}