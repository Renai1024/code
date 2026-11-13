package com.miracle.extra.validator;

import com.miracle.extra.domain.enums.BarcodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BarcodeValidatorFactory {

    private final Map<BarcodeType, BarcodeValidator> validatorMap = new HashMap<>();

    @Autowired
    public BarcodeValidatorFactory(List<BarcodeValidator> validators) {
        for (BarcodeValidator validator : validators) {
            validatorMap.put(validator.getSupportedType(), validator);
        }
    }

    public BarcodeValidator getValidator(BarcodeType type) {
        BarcodeValidator validator = validatorMap.get(type);
        if (validator == null) {
            throw new IllegalArgumentException("未找到条码类型对应的校验器: " + type);
        }
        return validator;
    }

    public void validate(BarcodeType type, String barcode, Object... params) {
        getValidator(type).validate(barcode, params);
    }

    public boolean isValid(BarcodeType type, String barcode, Object... params) {
        return getValidator(type).isValid(barcode, params);
    }
}