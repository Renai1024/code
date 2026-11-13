package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 立维斯气密性报表
 */
@Data
public class MesLWSQiMiXingReport {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private int id;

    /**
     * 数据创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date chuangjianshijian;

    /**
     * 检测日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "检测日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date jianceriqi;

    /**
     * 检测日期开始时间
     **/
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date testStartTime;

    /**
     * 检测日期结束时间
     **/
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date testEndTime;

    /** 工位 */
    @Excel(name = "工位")
    private String gongwei;

    /** 条码 */
    @Excel(name = "条码")
    private String tiaoma;

    /** 充气时间 */
    @Excel(name = "充气时间")
    private String chongqishijian;

    /** 稳压时间 */
    @Excel(name = "稳压时间")
    private String wenyashijian;

    /** 测试时间 */
    @Excel(name = "测试时间")
    private String ceshishijian;

    /** 测试压力 */
    @Excel(name = "测试压力")
    private String ceshiyali;

    /** 泄露值 */
    @Excel(name = "泄露值")
    private String xielouzhi;

    /** 结果 */
    @Excel(name = "结果")
    private String jieguo;
}