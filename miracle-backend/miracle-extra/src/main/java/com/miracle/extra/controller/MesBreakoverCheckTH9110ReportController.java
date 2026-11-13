package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesBreakoverCheckTH9110Report;
import com.miracle.extra.service.IMesBreakoverCheckTH9110ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Anonymous
@RequestMapping("/extra/mes/report/mes_report_breakoverCheckTH9110")
public class MesBreakoverCheckTH9110ReportController extends BaseController {

    @Autowired
    private IMesBreakoverCheckTH9110ReportService mesBreakoverCheckTH9110ReportService;

    /**
     * 查询绝缘耐压测试仪数据列表
     * @param mesBreakoverCheckTH9110Report
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(MesBreakoverCheckTH9110Report mesBreakoverCheckTH9110Report){
        startPage();
        List<MesBreakoverCheckTH9110Report> list = mesBreakoverCheckTH9110ReportService.selectMesBreakoverCheckTH9110ReportList(mesBreakoverCheckTH9110Report);
        return getDataTable(list);
    }

    /**
     * 导出绝缘耐压测试仪列表列表
     */
    @Log(title = "mesBreakoverCheckTH9110Report", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void mesBreakoverCheckTH9110ReportExport(HttpServletResponse response, MesBreakoverCheckTH9110Report mesBreakoverCheckTH9110Report)
    {
        List<MesBreakoverCheckTH9110Report> list = mesBreakoverCheckTH9110ReportService.selectMesBreakoverCheckTH9110ReportList(mesBreakoverCheckTH9110Report);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesBreakoverCheckTH9110Report> util = new ExcelUtil<MesBreakoverCheckTH9110Report>(MesBreakoverCheckTH9110Report.class);
        util.exportExcel(response, list, "绝缘耐压测试仪TH9110数据");
    }
}
