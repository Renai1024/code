package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesLWSQiMiXingReport;
import com.miracle.extra.service.IMesLWSQiMiXingReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Anonymous
@RequestMapping("/extra/mes/report/mes_report_lwsQiMiXing")
public class MesLWSQiMiXingReportController extends BaseController {

    @Autowired
    private IMesLWSQiMiXingReportService mesLWSQiMiXingReportService;

    /**
     * 查询立维斯气密性数据列表
     * @param mesLWSQiMiXingReport
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(MesLWSQiMiXingReport mesLWSQiMiXingReport){
        startPage();
        List<MesLWSQiMiXingReport> list = mesLWSQiMiXingReportService.selectMesLWSQiMiXingReportList(mesLWSQiMiXingReport);
        return getDataTable(list);
    }

    /**
     * 导出立维斯气密性列表列表
     */
    @Log(title = "mesLWSQiMiXingReport", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void mesLWSQiMiXingReportExport(HttpServletResponse response, MesLWSQiMiXingReport mesLWSQiMiXingReport)
    {
        List<MesLWSQiMiXingReport> list = mesLWSQiMiXingReportService.selectMesLWSQiMiXingReportList(mesLWSQiMiXingReport);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesLWSQiMiXingReport> util = new ExcelUtil<MesLWSQiMiXingReport>(MesLWSQiMiXingReport.class);
        util.exportExcel(response, list, "mesLWSQiMiXingReport数据");
    }
}
