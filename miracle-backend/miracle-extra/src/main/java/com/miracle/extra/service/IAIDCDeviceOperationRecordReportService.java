package com.miracle.extra.service;

import com.miracle.extra.domain.AIDCDeviceOperationRecordReport;
import com.miracle.extra.domain.BYDDeviceOperationRecordReport;

import java.util.List;

public interface IAIDCDeviceOperationRecordReportService {
    List<AIDCDeviceOperationRecordReport> selectAIDCDeviceOperationRecordReportList(AIDCDeviceOperationRecordReport report);
}