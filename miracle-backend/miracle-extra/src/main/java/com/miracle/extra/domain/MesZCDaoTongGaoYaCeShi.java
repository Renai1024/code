package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 中测导通高压测试
 */
@Data
public class MesZCDaoTongGaoYaCeShi {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private int id;

    /**
     * 站号
     */
    @Excel(name = "站号")
    private String stationId;

    /**
     * 设备编号
     */
    @Excel(name = "设备编号")
    private String machineId;

    /**
     * 条码
     */
    @Excel(name = "条码")
    private String sn;

    /**
     * 条码2
     */
    @Excel(name = "条码2")
    private String sn2;

    /**
     * 生产型号
     */
    @Excel(name = "生产型号")
    private String pn;

    /**
     * 订单号
     */
    @Excel(name = "订单号")
    private String orderId;

    /**
     * 批次号
     */
    @Excel(name = "批次号")
    private String batchId;

    /**
     * 作业员
     */
    @Excel(name = "作业员")
    private String operator;

    /**
     * 结果
     */
    @Excel(name = "结果")
    private String result;

    /**
     * 开始时间
     */
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 测试数据
     */
    @Excel(name = "测试数据")
    private String dataResult;

    /**
     * 记录创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 开始时间搜索条件
     **/
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date testStartTime;

    /**
     * 结束时间搜索条件
     **/
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date testEndTime;
}
