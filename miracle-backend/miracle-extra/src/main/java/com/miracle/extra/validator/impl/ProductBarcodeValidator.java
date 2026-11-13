package com.miracle.extra.validator.impl;

import com.miracle.extra.domain.ProductInfo;
import com.miracle.extra.domain.enums.BarcodeType;
import com.miracle.extra.service.IProductInfoService;
import com.miracle.extra.validator.BarcodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ProductBarcodeValidator implements BarcodeValidator {

    @Autowired
    private IProductInfoService productInfoService;

    @Override
    public BarcodeType getSupportedType() {
        return BarcodeType.PRODUCT_QR_CODE;
    }

    @Override
    public void validate(String barcode, Object... params) {
        if (barcode == null || barcode.trim().isEmpty()) {
            throw new IllegalArgumentException("产品二维码不能为空");
        }

        if (params == null || params.length == 0) {
            throw new IllegalArgumentException("缺少产品料号参数");
        }
        String productCode = (String) params[0];

        ProductInfo productInfo = productInfoService.getProductByCode(productCode);
        if (productInfo == null) {
            throw new IllegalArgumentException("产品料号不存在: " + productCode);
        }

        String barcodeRule = productInfo.getBarcodeRule();
        if (barcodeRule == null || barcodeRule.trim().isEmpty()) {
            throw new IllegalArgumentException("产品编码规则未配置: " + productCode);
        }

        if (!Pattern.matches(barcodeRule, barcode)) {
            throw new IllegalArgumentException("产品二维码格式不符合编码规则: " + barcodeRule);
        }
    }

    @Override
    public boolean isValid(String barcode, Object... params) {
        try {
            validate(barcode, params);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}