package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesZhanShengMangChaJiReport;
import com.miracle.extra.service.IMesZhanShengMangChaJiReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extra/mes/report/mes_report_zhanShengMangChaJi")
@Anonymous
public class MesZhanShengMangChaJiReportController extends BaseController {

    @Autowired
    private IMesZhanShengMangChaJiReportService mesZhanShengMangChaJiReportService;

    @GetMapping("/list")
    public TableDataInfo list(MesZhanShengMangChaJiReport report) {
        startPage();
        List<MesZhanShengMangChaJiReport> list = mesZhanShengMangChaJiReportService.selectMesZhanShengMangChaJiReportList(report);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, MesZhanShengMangChaJiReport report) {
        List<MesZhanShengMangChaJiReport> list = mesZhanShengMangChaJiReportService.selectMesZhanShengMangChaJiReportList(report);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesZhanShengMangChaJiReport> util = new ExcelUtil<MesZhanShengMangChaJiReport>(MesZhanShengMangChaJiReport.class);
        util.exportExcel(response, list, "展昇盲插机报表");
    }
}