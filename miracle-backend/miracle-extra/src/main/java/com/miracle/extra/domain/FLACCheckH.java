package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 对应纸飞机系统FLACCheckH表数据
 */
@Data
public class FLACCheckH implements Serializable {

    /**
     * 主键
     */
    private int id;

    /**
     * 测试结果
     */
    @Excel(name = "测试结果")
    private String ResultStr;

    /**
     * 检测开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "检测时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date CheckStartTime;

    /**
     * 检测结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CheckEndTime;

    @Excel(name = "StockInBarCode")
    private String StockInBarCode;

    /**
     * 模块标识
     */
    private String CustomerFlag;

    @Excel(name = "MyField1")
    private String MyField1;

    @Excel(name = "MyField2")
    private String MyField2;

    @Excel(name = "MyField3")
    private String MyField3;

    @Excel(name = "MyField4")
    private String MyField4;


    // ========== 查询条件字段（非数据库字段）==========

    /** 测试开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date testStartTime;

    /** 测试结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date testEndTime;
}
