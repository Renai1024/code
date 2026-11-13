package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesDianJiaoJiRecordReport;
import com.miracle.extra.service.IMesDianJiaoJiRecordReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Anonymous
@RequestMapping("/extra/mes/report/mes_report_dianjiaoji")
public class MesDianJiaoJiRecordReportController extends BaseController {

    @Autowired
    private IMesDianJiaoJiRecordReportService MesDianJiaoJiRecordReportService;

    /**
     * 查询点胶机数据列表
     * @param mesDianJiaoJiRecordReport
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(MesDianJiaoJiRecordReport mesDianJiaoJiRecordReport){
        startPage();
        List<MesDianJiaoJiRecordReport> list = MesDianJiaoJiRecordReportService.selectMesDZDuanziyajiejdList(mesDianJiaoJiRecordReport);
        return getDataTable(list);
    }

    /**
     * 导出点胶机列表列表
     */
    @Log(title = "MesDianJiaoJiRecordReport", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void mesDuanziyajieReportExport(HttpServletResponse response, MesDianJiaoJiRecordReport mesDianJiaoJiRecordReport)
    {
        List<MesDianJiaoJiRecordReport> list = MesDianJiaoJiRecordReportService.selectMesDZDuanziyajiejdList(mesDianJiaoJiRecordReport);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesDianJiaoJiRecordReport> util = new ExcelUtil<MesDianJiaoJiRecordReport>(MesDianJiaoJiRecordReport.class);
        util.exportExcel(response, list, "MesDianJiaoJiRecordReport数据");
    }
}
