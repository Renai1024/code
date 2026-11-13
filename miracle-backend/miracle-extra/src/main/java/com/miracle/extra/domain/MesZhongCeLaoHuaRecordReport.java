package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MesZhongCeLaoHuaRecordReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 工位ID */
    @Excel(name = "工位ID")
    private String stationId;

    /** 机器ID */
    @Excel(name = "机器ID")
    private String machineId;

    /** 序列号 */
    @Excel(name = "序列号")
    private String sn;

    /** 序列号2 */
    @Excel(name = "序列号2")
    private String sn2;

    /** 产品编号 */
    @Excel(name = "产品编号")
    private String pn;

    /** 订单ID */
    @Excel(name = "订单ID")
    private String orderId;

    /** 批次ID */
    @Excel(name = "批次ID")
    private String batchId;

    /** 用户 */
    @Excel(name = "用户")
    private String user;

    /** 结果 */
    @Excel(name = "结果")
    private String result;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 数据 */
    @Excel(name = "数据")
    private String data;

    // ========== 查询条件字段（非数据库字段）==========
    /** 创建开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createStartTime;

    /** 创建结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createEndTime;

    /** 开始时间范围 - 开始 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startStartTime;

    /** 开始时间范围 - 结束 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startEndTime;
}