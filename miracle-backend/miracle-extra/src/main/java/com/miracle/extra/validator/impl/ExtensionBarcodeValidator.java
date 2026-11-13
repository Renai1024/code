package com.miracle.extra.validator.impl;

import com.miracle.extra.domain.enums.BarcodeType;
import com.miracle.extra.validator.BarcodeValidator;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ExtensionBarcodeValidator implements BarcodeValidator {

    private static final Pattern EXTENSION_CODE_PATTERN = 
            Pattern.compile("^\\d{6}[A-Za-z]{4}\\d{3}$");

    @Override
    public BarcodeType getSupportedType() {
        return BarcodeType.EXTENSION_CODE;
    }

    @Override
    public void validate(String barcode, Object... params) {
        if (barcode == null || barcode.trim().isEmpty()) {
            throw new IllegalArgumentException("扩展码不能为空");
        }

        if (!EXTENSION_CODE_PATTERN.matcher(barcode).matches()) {
            throw new IllegalArgumentException("扩展码格式不正确，应为6位日期+4位字母+3位流水号，例如：260630QFAD127");
        }

        String datePart = barcode.substring(0, 6);
        try {
            int year = Integer.parseInt(datePart.substring(0, 2));
            int month = Integer.parseInt(datePart.substring(2, 4));
            int day = Integer.parseInt(datePart.substring(4, 6));
            
            if (month < 1 || month > 12 || day < 1 || day > 31) {
                throw new IllegalArgumentException("扩展码日期部分无效: " + datePart);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("扩展码日期部分格式错误: " + datePart);
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