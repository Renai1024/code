package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesDianCeXianReport;
import com.miracle.extra.service.IMesDianCeXianReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Anonymous
@RequestMapping("/extra/mes/report/mes_report_diancexian")
public class MesDianCeXianReportController extends BaseController {

    @Autowired
    private IMesDianCeXianReportService mesDianCeXianReportService;

    /**
     * 查询电测线数据列表
     * @param mesDianCeXianReport
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(MesDianCeXianReport mesDianCeXianReport){
        startPage();
        List<MesDianCeXianReport> list = mesDianCeXianReportService.selectMesDianCeXianReportList(mesDianCeXianReport);
        return getDataTable(list);
    }

    /**
     * 导出电测线列表列表
     */
    @Log(title = "mesDianCeXianReport", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void mesDianCeXianReportExport(HttpServletResponse response, MesDianCeXianReport mesDianCeXianReport)
    {
        List<MesDianCeXianReport> list = mesDianCeXianReportService.selectMesDianCeXianReportList(mesDianCeXianReport);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesDianCeXianReport> util = new ExcelUtil<MesDianCeXianReport>(MesDianCeXianReport.class);
        util.exportExcel(response, list, "mesDianCeXianReport数据");
    }
}
