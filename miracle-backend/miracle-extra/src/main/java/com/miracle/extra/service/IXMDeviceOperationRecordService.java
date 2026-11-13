package com.miracle.extra.service;

import com.miracle.extra.domain.XMDeviceOperationRecord;

import java.util.List;

public interface IXMDeviceOperationRecordService {
    List<XMDeviceOperationRecord> selectXMDeviceOperationRecordList(XMDeviceOperationRecord record);
}