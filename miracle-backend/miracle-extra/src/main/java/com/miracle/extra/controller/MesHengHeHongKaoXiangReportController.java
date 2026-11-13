package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesHengHeHongKaoXiangReport;
import com.miracle.extra.service.IMesHengHeHongKaoXiangReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extra/mes/report/mes_report_hengHeHongKaoXiang")
@Anonymous
public class MesHengHeHongKaoXiangReportController extends BaseController {

    @Autowired
    private IMesHengHeHongKaoXiangReportService mesHengHeHongKaoXiangReportService;

    @GetMapping("/list")
    public TableDataInfo list(MesHengHeHongKaoXiangReport report) {
        startPage();
        List<MesHengHeHongKaoXiangReport> list = mesHengHeHongKaoXiangReportService.selectMesHengHeHongKaoXiangReportList(report);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, MesHengHeHongKaoXiangReport report) {
        List<MesHengHeHongKaoXiangReport> list = mesHengHeHongKaoXiangReportService.selectMesHengHeHongKaoXiangReportList(report);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesHengHeHongKaoXiangReport> util = new ExcelUtil<MesHengHeHongKaoXiangReport>(MesHengHeHongKaoXiangReport.class);
        util.exportExcel(response, list, "横河烘箱报表");
    }
}