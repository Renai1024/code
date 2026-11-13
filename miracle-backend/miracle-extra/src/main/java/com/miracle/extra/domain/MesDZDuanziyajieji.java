package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 典帧端子压接机
 */
@Data
public class MesDZDuanziyajieji implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 测试时间（对应 DateTime）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "测试时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    /**
     * 压力峰值
     */
    @Excel(name = "压力峰值")
    private Double maxKg;

    /**
     * 峰值判定结果
     */
    @Excel(name = "峰值判定结果")
    private Double maxKgDif;

    /**
     * 峰值公差上限
     */
    @Excel(name = "峰值公差上限")
    private Double maxKgMaxDif;

    /**
     * 峰值公差下限
     */
    @Excel(name = "峰值公差下限")
    private Double maxKgMinDif;

    /**
     * 波峰偏移判定结果
     */
    @Excel(name = "波峰偏移判定结果")
    private Double maxKgPosDif;

    /**
     * 波峰偏移公差上限
     */
    @Excel(name = "波峰偏移公差上限")
    private Double maxKgPosMaxDif;

    /**
     * 波峰偏移公差下限
     */
    @Excel(name = "波峰偏移公差下限")
    private Double maxKgPosMinDif;

    /**
     * 面积判定结果
     */
    @Excel(name = "面积判定结果")
    private Double totalAreaDif;

    /**
     * 面积公差上限
     */
    @Excel(name = "面积公差上限")
    private Double totalAreaMaxDif;

    /**
     * 面积公差下限
     */
    @Excel(name = "面积公差下限")
    private Double totalAreaMinDif;

    /**
     * 少芯线判定结果
     */
    @Excel(name = "少芯线判定结果")
    private Double area3Dif;

    /**
     * 少芯线公差上限
     */
    @Excel(name = "少芯线公差上限")
    private Double area3MaxDif;

    /**
     * 少芯线公差下限
     */
    @Excel(name = "少芯线公差下限")
    private Double area3MinDif;

    /**
     * CPK
     */
    @Excel(name = "CPK")
    private Double cpk;

    /**
     * 安定性
     */
    @Excel(name = "安定性")
    private Double stability;

    /**
     * 压接高度
     */
    @Excel(name = "压接高度")
    private Double pressHigh;

    /**
     * 工站编号
     */
    @Excel(name = "工站编号")
    private String workStation;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称")
    private String deviceName;

    /**
     * 生产批次号
     */
    @Excel(name = "生产批次号")
    private String productNo;

    /**
     * 线材型号
     */
    @Excel(name = "线材型号")
    private String lineType;

    /**
     * 端子型号
     */
    @Excel(name = "端子型号")
    private String termType;

    /**
     * 模具型号
     */
    @Excel(name = "模具型号")
    private String modeType;

    /**
     * 检测结果
     */
    @Excel(name = "检测结果")
    private String result;

    /**
     * 数据创建时间（对应 CreatedAt）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    // ========== 以下为查询用的筛选字段，非数据库列 ==========

    /**
     * 检测日期开始时间（用于前端范围查询）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date testStartTime;

    /**
     * 检测日期结束时间（用于前端范围查询）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date testEndTime;
}
