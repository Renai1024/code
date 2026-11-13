package com.miracle.extra.domain;

import com.miracle.common.annotation.Excel;
import com.miracle.extra.domain.enums.BarcodeType;
import lombok.Data;
import java.util.Date;

@Data
public class BarcodeBindRecord {

    private Long id;
    
    private String bindUuid;
    
    @Excel(name = "产品料号")
    private String productCode;
    
    private String barcodeType1;
    
    @Excel(name = "产品二维码")
    private String barcodeValue1;
    
    private String barcodeType2;
    
    @Excel(name = "扩展码")
    private String barcodeValue2;
    
    @Excel(name = "设备编号")
    private String deviceNo;
    
    private String remark;
    
    @Excel(name = "绑定时间")
    private Date createTime;
    
    @Excel(name = "操作人")
    private String createBy;
    
    private Date modifyTime;
    private String modifyBy;
    private Boolean deleted;

    @Data
    public static class BindRequest {
        private String productCode;
        private String productQrCode;
        private String extensionCode;
        private String deviceNo;
    }

    @Data
    public static class ValidateRequest {
        private BarcodeType barcodeType;
        private String barcode;
        private String productCode;
        private String deviceNo;
    }
}