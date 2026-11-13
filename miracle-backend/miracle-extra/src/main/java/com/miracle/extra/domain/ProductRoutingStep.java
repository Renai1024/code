package com.miracle.extra.domain;

import lombok.Data;

@Data
public class ProductRoutingStep {
    private Integer processId;
    private Integer sequence;
    private Boolean barcodeVerify;
    private Boolean sequenceVerify;
    private Boolean duplicateVerify;
    private Integer requiredCount;
}