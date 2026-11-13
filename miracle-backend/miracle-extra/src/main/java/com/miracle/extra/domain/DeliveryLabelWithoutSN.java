package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DeliveryLabelWithoutSN implements Serializable {

    private static final long serialVersionUID = 1L;

    /** UUID主键 */
    private String fid;

    /** 客户外箱标签标识（如BYDOutBox） */
    private String customerFlag;

    /** 产品料号 */
    private String materialOldCode;

    /** 客户料号 */
    private String customerCode;

    /** 批次号 */
    private String lotNumber;

    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date manufactureDate;

    /** 条码 */
    private String barCodeStr;

    /** 序列号 */
    private String labelNumber;

    /** 箱装数 */
    private String customField1;

    /** IP地址 */
    private String customField2;

    /** 打印时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date labelPrintDateTime;
}
