package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesSheBeiZuDataReport;
import com.miracle.extra.service.IMesSheBeiZuDataReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Anonymous
@RequestMapping("/extra/mes/report/mes_report_shebeizu")
public class MesSheBeiZuDataReportController extends BaseController {

    @Autowired
    private IMesSheBeiZuDataReportService mesSheBeiZuDataReportService;

    /**
     * 查找线材导通测试数据列表
     * @param mesSheBeiZuDataReport
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(MesSheBeiZuDataReport mesSheBeiZuDataReport){
        startPage();
        List<MesSheBeiZuDataReport> list = mesSheBeiZuDataReportService.selectMesSheBeiZuDataReportList(mesSheBeiZuDataReport);
        return getDataTable(list);
    }

    /**
     * 导出线材导通测试数据列表
     */
    @Log(title = "mesSheBeiZuDataReport", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void mesSheBeiZuDataReportExport(HttpServletResponse response, MesSheBeiZuDataReport mesSheBeiZuDataReport)
    {
        List<MesSheBeiZuDataReport> list = mesSheBeiZuDataReportService.selectMesSheBeiZuDataReportList(mesSheBeiZuDataReport);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesSheBeiZuDataReport> util = new ExcelUtil<MesSheBeiZuDataReport>(MesSheBeiZuDataReport.class);
        util.exportExcel(response, list, "线材导通测试数据");
    }
}
