package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MesLiErLiChaBaLiReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Excel(name = "产品类型")
    private String productType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;

    @Excel(name = "产量")
    private Integer output;

    @Excel(name = "最大拉力值")
    private Integer tensionMaxValue;

    @Excel(name = "拉力上限")
    private Integer tensionUpperLimit;

    @Excel(name = "拉力下限")
    private Integer tensionLowerLimit;

    @Excel(name = "是否合格")
    private Boolean qualified;

    @Excel(name = "工单")
    private String workOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createEndTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationTimeStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationTimeEnd;
}