package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 端子压接机数据实体
 */
@Data
public class MesDuanziyajieReport implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 序号 */
    private String no;

    /** 流水号 */
    @Excel(name = "流水号")
    private String serialNumber;

    /** 数据生成时间（设备时间） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "设备时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    /** 产品条码 */
    @Excel(name = "产品条码")
    private String proUniqueCode;

    /** 线材类型 */
    @Excel(name = "线材类型")
    private String wireType;

    /** 压接模式 */
    @Excel(name = "压接模式")
    private String pressureWeldingMode;

    /** 铆接高度 */
    @Excel(name = "铆接高度")
    private String rivetingHeight;

    /** 铆接修正 */
    @Excel(name = "铆接修正")
    private String rivetingCorrection;

    /** 预压行程 */
    @Excel(name = "预压行程")
    private String preloadingTrip;

    /** 定位位置 */
    @Excel(name = "定位位置")
    private String locationLocation;

    /** 保压时间 */
    @Excel(name = "保压时间")
    private String holdingTime;

    /** 启动方式 */
    @Excel(name = "启动方式")
    private String startWay;

    /** 峰值负载 */
    @Excel(name = "峰值负载")
    private String peakLoad;

    /** 峰值偏差 */
    @Excel(name = "峰值偏差")
    private String peakDeviation;

    /** 峰值压接力 */
    @Excel(name = "峰值压接力")
    private String peakCrimpingLoad;

    /** 压力监控峰值 */
    @Excel(name = "压力监控峰值")
    private String pressureMonitoringPeak;

    /** 检测模式 */
    @Excel(name = "检测模式")
    private String detectPatterns;

    /** 铆接压力速度 */
    @Excel(name = "铆接压力速度")
    private String rivetingPressureSpeed;

    /** 公差编号 */
    @Excel(name = "公差编号")
    private String toleranceNumber;

    /** CPK值 */
    @Excel(name = "CPK值")
    private String cpk;

    /** 安定性 */
    @Excel(name = "安定性")
    private String security;

    /** 峰值范围 */
    @Excel(name = "峰值范围")
    private String peakRange;

    /** 峰值判定 */
    @Excel(name = "峰值判定")
    private String peakJudgement;

    /** 面积范围 */
    @Excel(name = "面积范围")
    private String areaRange;

    /** 面积判定 */
    @Excel(name = "面积判定")
    private String areaJudgement;

    /** 偏移范围 */
    @Excel(name = "偏移范围")
    private String offsetRange;

    /** 偏移判定 */
    @Excel(name = "偏移判定")
    private String offsetJudgement;

    /** 少芯线范围 */
    @Excel(name = "少芯线范围")
    private String lessCoreWireRange;

    /** 少芯线判定 */
    @Excel(name = "少芯线判定")
    private String lessCoreWireJudgement;

    /** 采样窗口宽度 */
    @Excel(name = "采样窗口宽度")
    private String samplingWindowWidth;

    /** 结果（OK/NG） */
    @Excel(name = "测试结果")
    private String result;

    /** 操作员 */
    @Excel(name = "操作员")
    private String operators;

    /** 管理员 */
    @Excel(name = "管理员")
    private String administrator;

    /** 申请单号 */
    @Excel(name = "申请单号")
    private String applicationNo;

    /** 设备编号 */
    @Excel(name = "设备编号")
    private String equipmentNo;

    /** 数据库创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    // ========== 查询条件字段（非数据库字段）==========
    /** 测试开始时间（用于DateTime范围查询） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date testStartTime;

    /** 测试结束时间（用于DateTime范围查询） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date testEndTime;
}
