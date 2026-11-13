package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesBreakoverCheckTH8601Report;
import com.miracle.extra.service.IMesBreakoverCheckTH8601ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Anonymous
@RequestMapping("/extra/mes/report/mes_report_breakoverCheckTH8601")
public class MesBreakoverCheckTH8601ReportController extends BaseController {

    @Autowired
    private IMesBreakoverCheckTH8601ReportService mesBreakoverCheckTH8601ReportService;

    /**
     * 查询精密综合测试仪数据列表
     * @param mesBreakoverCheckTH8601Report
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(MesBreakoverCheckTH8601Report mesBreakoverCheckTH8601Report){
        startPage();
        List<MesBreakoverCheckTH8601Report> list = mesBreakoverCheckTH8601ReportService.selectMesBreakoverCheckTH8601ReportList(mesBreakoverCheckTH8601Report);
        return getDataTable(list);
    }

    /**
     * 导出精密综合测试仪列表列表
     */
    @Log(title = "mesBreakoverCheckTH8601Report", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void mesBreakoverCheckTH8601ReportExport(HttpServletResponse response, MesBreakoverCheckTH8601Report mesBreakoverCheckTH8601Report)
    {
        List<MesBreakoverCheckTH8601Report> list = mesBreakoverCheckTH8601ReportService.selectMesBreakoverCheckTH8601ReportList(mesBreakoverCheckTH8601Report);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesBreakoverCheckTH8601Report> util = new ExcelUtil<MesBreakoverCheckTH8601Report>(MesBreakoverCheckTH8601Report.class);
        util.exportExcel(response, list, "精密综合测试仪TH8601数据");
    }
}
