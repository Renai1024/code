package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MesMuPai3DataReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 所属类型（保留字段，不导出/不显示） */
    private String customerFlag;

    /** 产品料号 */
    @Excel(name = "产品料号")
    private String materialCode;

    /** 测试开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "测试时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date checkStartTime;

    /** 测试结束时间（保留字段，不导出/不显示） */
    private Date checkEndTime;

    /** 测试数据 */
    @Excel(name = "测试数据")
    private String resultStr;

    /** 条码 */
    @Excel(name = "条码")
    private String stockInBarCode;

    /** 机器编号 */
    @Excel(name = "机器编号")
    private String myField1;

    /** 保留字段2（不导出/不显示） */
    private String myField2;

    /** 保留字段3（不导出/不显示） */
    private String myField3;

    // ========== 查询条件字段（非数据库字段）==========
    /** 测试开始时间-起始 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTimeStart;

    /** 测试开始时间-结束 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTimeEnd;

    /** 条码查询 */
    private String barCodeQuery;

    /** 产品料号查询 */
    private String materialCodeQuery;

    /** 机器编号查询 */
    private String machineNoQuery;
}
