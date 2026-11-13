package com.miracle.extra.service;

import com.miracle.extra.domain.BYDDeviceOperationRecordReport;

import java.util.List;

public interface IBYDDeviceOperationRecordReportService {
    List<BYDDeviceOperationRecordReport> selectBYDDeviceOperationRecordReportList(BYDDeviceOperationRecordReport report);
}