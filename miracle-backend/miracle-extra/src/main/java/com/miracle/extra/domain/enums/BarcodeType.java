package com.miracle.extra.domain.enums;

public enum BarcodeType {
    PRODUCT_QR_CODE("PRODUCT_QR_CODE", "产品二维码"),
    EXTENSION_CODE("EXTENSION_CODE", "扩展码"),
    OUTER_BOX_CODE("OUTER_BOX_CODE", "外箱码"),
    SERIAL_NUMBER("SERIAL_NUMBER", "序列号"),
    /** 客户外箱标签码（客户出货标签绑定场景专用，存储完整外箱标签字符串） */
    CUSTOMER_OUTER_BOX("CUSTOMER_OUTER_BOX", "客户外箱标签码");

    private final String code;
    private final String description;

    BarcodeType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static BarcodeType fromCode(String code) {
        for (BarcodeType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的条码类型: " + code);
    }
}