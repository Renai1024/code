package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MesChuXinNiuZhuanHanReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 记录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "记录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 条码 */
    @Excel(name = "条码")
    private String batchNo;

    /** 生产时间 */
    @Excel(name = "生产时间")
    private String productionTime;

    /** 产品料号 */
    @Excel(name = "产品料号")
    private String productCode;

    /** 焊接时间(ms) */
    @Excel(name = "焊接时间(ms)")
    private Double weldingTime;

    /** 焊接振幅(%) */
    @Excel(name = "焊接振幅(%)")
    private Double weldingAmplitude;

    /** 焊接压力(Mpa) */
    @Excel(name = "焊接压力(Mpa)")
    private Double weldingPressure;

    /** 焊接能耗(J) */
    @Excel(name = "焊接能耗(J)")
    private Double weldingEnergy;

    /** 焊接功率(J) */
    @Excel(name = "焊接功率(J)")
    private Double weldingPower;

    /** 预压高度(mm) */
    @Excel(name = "预压高度(mm)")
    private Double preloadingHeight;

    /** 焊后高度(mm) */
    @Excel(name = "焊后高度(mm)")
    private Double postWeldingHeight;

    /** 焊接深度(mm) */
    @Excel(name = "焊接深度(mm)")
    private Double weldingDepth;

    /** 运行模式 */
    @Excel(name = "运行模式")
    private String operationMode;

    /** 工作模式 */
    @Excel(name = "工作模式")
    private String workingMode;

    /** 时间上限(ms) */
    @Excel(name = "时间上限(ms)")
    private Double timeUpperLimit;

    /** 时间下限(ms) */
    @Excel(name = "时间下限(ms)")
    private Double timeLowerLimit;

    /** 功率上限(W) */
    @Excel(name = "功率上限(W)")
    private Double powerUpperLimit;

    /** 功率下限(W) */
    @Excel(name = "功率下限(W)")
    private Double powerLowerLimit;

    /** 能量上限(J) */
    @Excel(name = "能量上限(J)")
    private Double energyUpperLimit;

    /** 能量下限(J) */
    @Excel(name = "能量下限(J)")
    private Double energyLowerLimit;

    /** 预压上限(mm) */
    @Excel(name = "预压上限(mm)")
    private Double preloadingUpperLimit;

    /** 预压下限(mm) */
    @Excel(name = "预压下限(mm)")
    private Double preloadingLowerLimit;

    /** 焊后上限(mm) */
    @Excel(name = "焊后上限(mm)")
    private Double postWeldingUpperLimit;

    /** 焊后下限(mm) */
    @Excel(name = "焊后下限(mm)")
    private Double postWeldingLowerLimit;

    /** 焊头代码 */
    @Excel(name = "焊头代码")
    private String hornCode;

    /** 底砧代码 */
    @Excel(name = "底砧代码")
    private String anvilCode;

    /** 焊头使用次数 */
    @Excel(name = "焊头使用次数")
    private Double hornUseCount;

    /** 砧座使用次数 */
    @Excel(name = "砧座使用次数")
    private Double anvilUseCount;

    /** 报警状态 */
    @Excel(name = "报警状态")
    private String alarmStatus;

    /** 厂家设备编号 */
    @Excel(name = "厂家设备编号")
    private String equipmentNumber;

    /** 内部设备编号 **/
    @Excel(name = "内部设备编号")
    private String EquipmentNo;

    /** 工单 */
    @Excel(name = "工单")
    private String WorkOrder;

    /** 物料型号1 */
    @Excel(name = "物料型号1")
    private String MaterialModel1;

    /** 物料批次1 */
    @Excel(name = "物料批次1")
    private String MaterialBatch1;

    /** 物料型号2 */
    @Excel(name = "物料型号2")
    private String MaterialModel2;

    /** 物料批次2 */
    @Excel(name = "物料批次2")
    private String MaterialBatch2;

    /** 物料型号3 */
    @Excel(name = "物料型号3")
    private String MaterialModel3;

    /** 物料批次3 */
    @Excel(name = "物料批次3")
    private String MaterialBatch3;

    /** 物料型号4 */
    @Excel(name = "物料型号4")
    private String MaterialModel4;

    /** 物料批次4 */
    @Excel(name = "物料批次4")
    private String MaterialBatch4;

    /** 物料型号5 */
    @Excel(name = "物料型号5")
    private String MaterialModel5;

    /** 物料批次5 */
    @Excel(name = "物料批次5")
    private String MaterialBatch5;

    // ========== 查询条件字段（非数据库字段）==========
    /** 创建开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createStartTime;

    /** 创建结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createEndTime;
}