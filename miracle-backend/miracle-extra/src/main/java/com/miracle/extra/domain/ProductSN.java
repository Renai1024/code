package com.miracle.extra.domain;

import lombok.Data;

@Data
public class ProductSN {
    private Integer id;
    private String barcode;
    private Integer productId;
    private Integer productRoutingId;
    private Integer status;
    private Integer lastProcessId;
    private String nextProcessIds;
}