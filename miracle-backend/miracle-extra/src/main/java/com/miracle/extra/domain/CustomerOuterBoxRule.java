package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户外箱标签解析规则
 * 不同客户的外箱标签使用不同的正则表达式反解析出各业务字段（客户料号、批次号、生产日期等）
 * 一个客户可配置多条规则项，每条规则项负责解析一个字段，通过 customerCode 关联
 * 校验字段识别规则：
 *   服务层通过 fieldName = "customerPartNumber" 识别校验字段（即客户料号），
 *   该字段的解析值将与出货标签（vwOutBoxLabelRecord.CustomerCode）进行比对。
 *   如需调整校验字段，只需修改 fieldName 命名约定，无需新增列。
 *
 * @author miracle
 */
@Data
public class CustomerOuterBoxRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 客户编码 */
    @Excel(name = "客户编码")
    private String customerCode;

    /** 客户名称 */
    @Excel(name = "客户名称")
    private String customerName;

    /** 客户外箱标签标识（对应 vwOutBoxLabelRecord.CustomerFlag，如 BYDOutBox） */
    @Excel(name = "客户标签标识")
    private String customerFlag;

    /** 业务字段名（约定命名，用于程序识别字段用途，如 customerPartNumber、lotNumber 等） */
    @Excel(name = "业务字段名")
    private String fieldName;

    /** 业务字段中文名（展示用，如 客户料号、批次号） */
    @Excel(name = "字段中文名")
    private String fieldLabel;

    /** 标签正则表达式（用于从外箱标签中反解析对应字段值，如 M:([^/;]+)） */
    @Excel(name = "正则表达式")
    private String labelRegex;

    /** 提取组号（正则捕获组序号，默认1；当正则有多个捕获组时，指定取第N组） */
    @Excel(name = "提取组号")
    private Integer extractGroup;

    /** 排序号（解析/展示顺序，从小到大） */
    @Excel(name = "排序号")
    private Integer sortOrder;

    /** 启用状态（1=启用 0=停用） */
    @Excel(name = "启用状态")
    private Integer activeStatus;

    /** 备注 */
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;

    private String modifyBy;

    private Boolean deleted;
}
