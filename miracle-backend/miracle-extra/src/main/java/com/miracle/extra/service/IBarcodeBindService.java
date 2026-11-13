package com.miracle.extra.service;

import com.miracle.extra.domain.BarcodeBindRecord;
import com.miracle.extra.domain.enums.BarcodeType;

import java.util.List;

public interface IBarcodeBindService {
    
    void validateBarcode(BarcodeType barcodeType, String barcode, String productCode, String deviceNo);
    
    void validateDuplicate(BarcodeType barcodeType, String barcode);
    
    BarcodeBindRecord bind(BarcodeBindRecord.BindRequest request);
    
    BarcodeBindRecord getByBindUuid(String bindUuid);
    
    List<BarcodeBindRecord> selectBindRecordList(BarcodeBindRecord record);
    
    int deleteBindRecordById(Long id);
    
    List<BarcodeBindRecord> selectByBarcode(BarcodeType barcodeType, String barcode);
}