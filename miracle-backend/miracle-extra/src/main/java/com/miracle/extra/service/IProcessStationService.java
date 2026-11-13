package com.miracle.extra.service;

public interface IProcessStationService {
    
    void verifyStation(String barcode, String equipmentNo, String productCode);
    
    void processStation(String barcode, String equipmentNo, String productCode, String dataTraceId);

    void processStation(String barcode, String equipmentNo, String productCode,boolean isPass, String defectReason, String dataTraceId);
}