package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesPressStrippingIntegratedMachineReport;
import com.miracle.extra.service.IMesPressStrippingIntegratedMachineReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extra/mes/report/mes_report_pressStrippingIntegratedMachine")
@Anonymous
public class MesPressStrippingIntegratedMachineReportController extends BaseController {

    @Autowired
    private IMesPressStrippingIntegratedMachineReportService mesPressStrippingIntegratedMachineReportService;

    @GetMapping("/list")
    public TableDataInfo list(MesPressStrippingIntegratedMachineReport report) {
        startPage();
        List<MesPressStrippingIntegratedMachineReport> list = mesPressStrippingIntegratedMachineReportService.selectMesPressStrippingIntegratedMachineReportList(report);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, MesPressStrippingIntegratedMachineReport report) {
        List<MesPressStrippingIntegratedMachineReport> list = mesPressStrippingIntegratedMachineReportService.selectMesPressStrippingIntegratedMachineReportList(report);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesPressStrippingIntegratedMachineReport> util = new ExcelUtil<MesPressStrippingIntegratedMachineReport>(MesPressStrippingIntegratedMachineReport.class);
        util.exportExcel(response, list, "压剥一体机报表");
    }
}
