package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesLiErLiChaBaLiReport;
import com.miracle.extra.service.IMesLiErLiChaBaLiReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extra/mes/report/mes_report_liErLiChaBaLi")
@Anonymous
public class MesLiErLiChaBaLiReportController extends BaseController {

    @Autowired
    private IMesLiErLiChaBaLiReportService mesLiErLiChaBaLiReportService;

    @GetMapping("/list")
    public TableDataInfo list(MesLiErLiChaBaLiReport report) {
        startPage();
        List<MesLiErLiChaBaLiReport> list = mesLiErLiChaBaLiReportService.selectMesLiErLiChaBaLiReportList(report);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, MesLiErLiChaBaLiReport report) {
        List<MesLiErLiChaBaLiReport> list = mesLiErLiChaBaLiReportService.selectMesLiErLiChaBaLiReportList(report);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesLiErLiChaBaLiReport> util = new ExcelUtil<MesLiErLiChaBaLiReport>(MesLiErLiChaBaLiReport.class);
        util.exportExcel(response, list, "立而利插拔力报表");
    }
}