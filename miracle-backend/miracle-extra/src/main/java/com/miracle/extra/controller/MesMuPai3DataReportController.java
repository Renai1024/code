package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesMuPai3DataReport;
import com.miracle.extra.service.IMesMuPai3DataReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/extra/mes/report/mes_report_mupai3")
@Anonymous
public class MesMuPai3DataReportController extends BaseController {

    @Autowired
    private IMesMuPai3DataReportService service;

    @GetMapping("/hsnycs/list")
    public TableDataInfo list(MesMuPai3DataReport report) {
        startPage();
        List<MesMuPai3DataReport> list = service.selectHSnycsDataReportList(report);
        return getDataTable(list);
    }

    @Log(title = "mesHSnycsDataReport", businessType = BusinessType.EXPORT)
    @PostMapping("/hsnycs/export")
    public void export(HttpServletResponse response, MesMuPai3DataReport report) {
        List<MesMuPai3DataReport> list = service.selectHSnycsDataReportList(report);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesMuPai3DataReport> util = new ExcelUtil<MesMuPai3DataReport>(MesMuPai3DataReport.class);
        util.exportExcel(response, list, "海思耐压测试数据");
    }
}