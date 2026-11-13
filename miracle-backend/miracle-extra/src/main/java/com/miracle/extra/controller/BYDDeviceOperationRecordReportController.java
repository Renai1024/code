package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.BYDDeviceOperationRecordReport;
import com.miracle.extra.service.IBYDDeviceOperationRecordReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extra/mes/report/mes_report_byd_device_operation_record")
@Anonymous
public class BYDDeviceOperationRecordReportController extends BaseController {

    @Autowired
    private IBYDDeviceOperationRecordReportService bydDeviceOperationRecordReportService;

    @GetMapping("/list")
    public TableDataInfo list(BYDDeviceOperationRecordReport report) {
        startPage();
        List<BYDDeviceOperationRecordReport> list = bydDeviceOperationRecordReportService.selectBYDDeviceOperationRecordReportList(report);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, BYDDeviceOperationRecordReport report) {
        List<BYDDeviceOperationRecordReport> list = bydDeviceOperationRecordReportService.selectBYDDeviceOperationRecordReportList(report);
        if (list.size() > 5000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<BYDDeviceOperationRecordReport> util = new ExcelUtil<BYDDeviceOperationRecordReport>(BYDDeviceOperationRecordReport.class);
        util.exportExcel(response, list, "比亚迪设备操作记录报表");
    }
}