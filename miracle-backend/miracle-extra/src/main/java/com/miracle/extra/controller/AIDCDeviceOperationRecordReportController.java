package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.AIDCDeviceOperationRecordReport;
import com.miracle.extra.domain.BYDDeviceOperationRecordReport;
import com.miracle.extra.service.IAIDCDeviceOperationRecordReportService;
import com.miracle.extra.service.IBYDDeviceOperationRecordReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/extra/mes/report/mes_report_aidc_device_operation_record")
@Anonymous
public class AIDCDeviceOperationRecordReportController extends BaseController {

    @Autowired
    private IAIDCDeviceOperationRecordReportService aidcDeviceOperationRecordReportService;

    @GetMapping("/list")
    public TableDataInfo list(AIDCDeviceOperationRecordReport report) {
        startPage();
        List<AIDCDeviceOperationRecordReport> list = aidcDeviceOperationRecordReportService.selectAIDCDeviceOperationRecordReportList(report);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, AIDCDeviceOperationRecordReport report) {
        List<AIDCDeviceOperationRecordReport> list = aidcDeviceOperationRecordReportService.selectAIDCDeviceOperationRecordReportList(report);
        if (list.size() > 100) {
            throw new BaseException("数据量大，请分批导出或联系管理员导出");
        }
        ExcelUtil<AIDCDeviceOperationRecordReport> util = new ExcelUtil<AIDCDeviceOperationRecordReport>(AIDCDeviceOperationRecordReport.class);
        util.exportExcel(response, list, "设备操作记录报表(AIDC)");
    }
}