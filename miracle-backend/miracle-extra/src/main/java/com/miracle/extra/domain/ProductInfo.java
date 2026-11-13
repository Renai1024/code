package com.miracle.extra.domain;

import lombok.Data;
import java.util.Date;

@Data
public class ProductInfo {
    private Integer id;
    private String customerFlag;
    private String customerProductCode;
    private String productCode;
    private String productName;
    private String specification;
    private String productVersion;
    private String ddFactoryFlag;
    private String barcodeRule;
    private String remark;
    private Integer activeStatus;
    private Date createTime;
    private String createBy;
    private Date modifyTime;
    private String modifyBy;
    private Boolean deleted;
}