package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MesPressStrippingIntegratedMachineReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Excel(name = "编号")
    private String no;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;

    @Excel(name = "工单")
    private String workOrder;

    @Excel(name = "端子名称")
    private String terminalName;

    @Excel(name = "产品名称")
    private String productName;

    @Excel(name = "额定压缩高度")
    private String ratedCompressionHeight;

    @Excel(name = "CFA")
    private String cfa;

    @Excel(name = "CFM阈值")
    private String cfmThreshold;

    @Excel(name = "压力")
    private String pressure;

    @Excel(name = "状态")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createEndTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationTimeStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationTimeEnd;
}
