package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesZhongCeChargeModeReport;
import com.miracle.extra.service.IMesZhongCeChargeModeReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extra/mes/report/mes_report_zhongCeChargeMode")
@Anonymous
public class MesZhongCeChargeModeReportController extends BaseController {

    @Autowired
    private IMesZhongCeChargeModeReportService mesZhongCeChargeModeReportService;

    @GetMapping("/list")
    public TableDataInfo list(MesZhongCeChargeModeReport report) {
        startPage();
        List<MesZhongCeChargeModeReport> list = mesZhongCeChargeModeReportService.selectMesZhongCeChargeModeReportList(report);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, MesZhongCeChargeModeReport report) {
        List<MesZhongCeChargeModeReport> list = mesZhongCeChargeModeReportService.selectMesZhongCeChargeModeReportList(report);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesZhongCeChargeModeReport> util = new ExcelUtil<MesZhongCeChargeModeReport>(MesZhongCeChargeModeReport.class);
        util.exportExcel(response, list, "中测充电模式二测试机报表");
    }
}