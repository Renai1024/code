package com.miracle.extra.domain;

import lombok.Data;

@Data
public class StationRecord {
    private Integer processId;
    private Integer result;
    private String barcode;
    private Integer productId;
    private Integer productRoutingId;
    private Integer equipmentId;
    private String resultDetail;
    private String dataTraceId;
    private java.util.Date createTime;
    private Boolean isReworkVoid;
}