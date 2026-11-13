package com.miracle.extra.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 万肯气密性CPK分析报告
 * <p>
 * 基于FLACCheckH表数据，按生产工单号进行CPK计算分析的结果对象
 * CPK计算公式：CPK = (控制标准上限 - 实测样本均值) / (3 * 实测样本标准差)
 * </p>
 */
@Data
public class WKCheckPrintCpkReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 生产工单号
     * 对应FLACCheckH表的MyField3字段
     */
    private String workOrderNo;

    /**
     * 设备编号
     * 对应FLACCheckH表的MyField4字段，可选字段
     */
    private String deviceNo;

    /**
     * 控制标准上限
     * 对应FLACCheckH表的MyField2字段，用于CPK计算
     */
    private Double controlUpperLimit;

    /**
     * 有效样本数量
     * 指成功提取泄漏量数值且控制上限有效的数据条数
     */
    private Integer sampleCount;

    /**
     * 合格数量
     * ResultStr中包含"合格"字样的数据条数
     */
    private Integer qualifiedCount;

    /**
     * 不合格数量
     * ResultStr中包含"不合格"字样的数据条数
     */
    private Integer unqualifiedCount;

    /**
     * 合格率（%）
     * 计算公式：合格数量 / (合格数量 + 不合格数量) * 100
     * 保留两位小数
     */
    private Double qualifiedRate;

    /**
     * 实测样本均值（绝对值）
     * 所有有效样本泄漏量绝对值的平均值
     * 保留6位小数
     */
    private Double meanValue;

    /**
     * 实测样本标准差
     * 使用样本标准差公式计算（除以n-1）
     * 保留6位小数
     */
    private Double stdDeviation;

    /**
     * CPK值
     * 计算公式：(控制标准上限 - 实测样本均值) / (3 * 实测样本标准差)
     * 保留3位小数
     * 当标准差为0时，CPK值为null
     */
    private Double cpkValue;

    /**
     * 最小值（绝对值）
     * 所有有效样本泄漏量绝对值中的最小值
     * 保留6位小数
     */
    private Double minValue;

    /**
     * 最大值（绝对值）
     * 所有有效样本泄漏量绝对值中的最大值
     * 保留6位小数
     */
    private Double maxValue;

    /**
     * 最大连续不良数
     * 指连续出现不合格产品的最大次数
     */
    private Integer maxConsecutiveDefects;

    /**
     * 最大连续不良开始时间
     * 最大连续不良时间段的起始时间
     */
    private java.util.Date maxConsecutiveDefectsStartTime;

    /**
     * 最大连续不良结束时间
     * 最大连续不良时间段的结束时间
     */
    private java.util.Date maxConsecutiveDefectsEndTime;
}