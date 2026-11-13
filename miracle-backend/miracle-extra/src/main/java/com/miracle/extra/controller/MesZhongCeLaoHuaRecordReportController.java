package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesZhongCeLaoHuaRecordReport;
import com.miracle.extra.service.IMesZhongCeLaoHuaRecordReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extra/mes/report/mes_report_zhongCeLaoHuaRecord")
@Anonymous
public class MesZhongCeLaoHuaRecordReportController extends BaseController {

    @Autowired
    private IMesZhongCeLaoHuaRecordReportService mesZhongCeLaoHuaRecordReportService;

    @GetMapping("/list")
    public TableDataInfo list(MesZhongCeLaoHuaRecordReport report) {
        startPage();
        List<MesZhongCeLaoHuaRecordReport> list = mesZhongCeLaoHuaRecordReportService.selectMesZhongCeLaoHuaRecordReportList(report);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, MesZhongCeLaoHuaRecordReport report) {
        List<MesZhongCeLaoHuaRecordReport> list = mesZhongCeLaoHuaRecordReportService.selectMesZhongCeLaoHuaRecordReportList(report);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesZhongCeLaoHuaRecordReport> util = new ExcelUtil<MesZhongCeLaoHuaRecordReport>(MesZhongCeLaoHuaRecordReport.class);
        util.exportExcel(response, list, "中测老化测试记录报表");
    }
}