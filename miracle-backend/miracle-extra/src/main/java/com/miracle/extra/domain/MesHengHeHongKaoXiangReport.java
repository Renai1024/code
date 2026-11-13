package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MesHengHeHongKaoXiangReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 事件类型 */
    @Excel(name = "事件类型")
    private String eventType;

    /** 上传时间 */
    @Excel(name = "上传时间")
    private String eventTime;

    /** 本次运行唯一标识 */
    @Excel(name = "运行标识")
    private String runId;

    /** 当前登录用户名 */
    @Excel(name = "用户名")
    private String userName;

    /** 开锁温度阈值 */
    @Excel(name = "开锁温度")
    private String unlockTemperature;

    /** 启动时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "启动时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 停止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "停止时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date stopTime;

    /** 实时数据快照 */
    @Excel(name = "实时数据快照")
    private String latestSnapshot;

    /** 样品列表 */
    @Excel(name = "样品列表")
    private String samplesJson;

    /** 样品列表 */
    private List<HengHeHongKaoXiangItem> items;

    // ========== 查询条件字段（非数据库字段）==========
    /** 创建开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createStartTime;

    /** 创建结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createEndTime;

    /** 启动开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startStartTime;

    /** 启动结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startEndTime;

    /** 样品编号 */
    private String sampleCode;

    @Data
    public static class HengHeHongKaoXiangItem implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 样品编号 */
        @Excel(name = "样品编号")
        private String sampleCode;

        /** 样品添加时间 */
        @Excel(name = "样品添加时间")
        private String addedAt;
    }
}