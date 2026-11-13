package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MesDianJiaoJiRecordReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    /** 条码 */
    @Excel(name = "条码")
    private String tiaoma;

    /** 灌胶时间 */
    @Excel(name = "灌胶时间")
    private String guanjiaoshijian;

    /** 点胶速度 */
    @Excel(name = "点胶速度")
    private String dianjiaosudu;

    /** 设备ID */
    @Excel(name = "设备ID")
    private String deviceId;

    /** 工作日期（字符串格式） */
    @Excel(name = "工作日期", width = 20)
    private String workDate;

    /** 工作结果 */
    @Excel(name = "工作结果")
    private String workResults;

    // ========== 查询条件字段（非数据库字段）==========
    /** 测试开始时间（用于DateTime范围查询） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date testStartTime;

    /** 测试结束时间（用于DateTime范围查询） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date testEndTime;
}