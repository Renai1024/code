package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesDengLiZiQingXiReport;
import com.miracle.extra.service.IMesDengLiZiQingXiReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extra/mes/report/mes_report_dengLiZiQingXi")
@Anonymous
public class MesDengLiZiQingXiReportController extends BaseController {

    @Autowired
    private IMesDengLiZiQingXiReportService mesDengLiZiQingXiReportService;

    @GetMapping("/list")
    public TableDataInfo list(MesDengLiZiQingXiReport report) {
        startPage();
        List<MesDengLiZiQingXiReport> list = mesDengLiZiQingXiReportService.selectMesDengLiZiQingXiReportList(report);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, MesDengLiZiQingXiReport report) {
        List<MesDengLiZiQingXiReport> list = mesDengLiZiQingXiReportService.selectMesDengLiZiQingXiReportList(report);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesDengLiZiQingXiReport> util = new ExcelUtil<MesDengLiZiQingXiReport>(MesDengLiZiQingXiReport.class);
        util.exportExcel(response, list, "等离子清洗机报表");
    }
}