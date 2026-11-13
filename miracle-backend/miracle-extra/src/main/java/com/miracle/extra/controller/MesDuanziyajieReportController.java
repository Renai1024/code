package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesDuanziyajieReport;
import com.miracle.extra.service.IMesDuanziyajieReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Anonymous
@RequestMapping("/extra/mes/report/mes_report_duanziyajie")
public class MesDuanziyajieReportController extends BaseController {

    @Autowired
    private IMesDuanziyajieReportService mesDuanziyajieReportService;

    /**
     * 查询端子压接机数据列表
     * @param mesDuanziyajieReport
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(MesDuanziyajieReport mesDuanziyajieReport){
        startPage();
        List<MesDuanziyajieReport> list = mesDuanziyajieReportService.selectMesDuanziyajieReportList(mesDuanziyajieReport);
        return getDataTable(list);
    }

    /**
     * 导出端子压接机列表列表
     */
    @Log(title = "mesDuanziyajieReport", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void mesDuanziyajieReportExport(HttpServletResponse response, MesDuanziyajieReport mesDuanziyajieReport)
    {
        List<MesDuanziyajieReport> list = mesDuanziyajieReportService.selectMesDuanziyajieReportList(mesDuanziyajieReport);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesDuanziyajieReport> util = new ExcelUtil<MesDuanziyajieReport>(MesDuanziyajieReport.class);
        util.exportExcel(response, list, "mesDuanziyajieReport数据");
    }
}
