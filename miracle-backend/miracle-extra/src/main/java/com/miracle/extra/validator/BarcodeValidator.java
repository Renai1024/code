package com.miracle.extra.validator;

import com.miracle.extra.domain.enums.BarcodeType;

public interface BarcodeValidator {
    BarcodeType getSupportedType();
    
    void validate(String barcode, Object... params);
    
    boolean isValid(String barcode, Object... params);
}