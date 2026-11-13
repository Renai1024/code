package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesChuXinDuanZiHanReport;
import com.miracle.extra.domain.MesChuXinNiuZhuanHanReport;
import com.miracle.extra.service.IMesChuXinDuanZiHanReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Anonymous
@RequestMapping("/extra/mes/report/mes_report_chuXinDuanZiHan")
public class MesChuXinDuanZiHanReportController extends BaseController {

//    @Autowired
//    private IMesChuXinDuanZiHanReportService mesChuXinDuanZiHanReportService;
//
//    /**
//     * 查询楚鑫端子焊接机数据列表
//     * @param mesChuXinDuanZiHanReport
//     * @return
//     */
//    @GetMapping("/list")
//    public TableDataInfo list(MesChuXinNiuZhuanHanReport mesChuXinDuanZiHanReport){
//        startPage();
//        List<MesChuXinNiuZhuanHanReport> list = mesChuXinDuanZiHanReportService.selectMesChuXinDuanZiHanReportList(mesChuXinDuanZiHanReport);
//        return getDataTable(list);
//    }
//
//    /**
//     * 导出楚鑫端子焊接机数据列表
//     */
//    @Log(title = "mesChuXinDuanZiHanReport", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void mesChuXinDuanZiHanReportExport(HttpServletResponse response, MesChuXinNiuZhuanHanReport mesChuXinDuanZiHanReport)
//    {
//        List<MesChuXinNiuZhuanHanReport> list = mesChuXinDuanZiHanReportService.selectMesChuXinDuanZiHanReportList(mesChuXinDuanZiHanReport);
//        ExcelUtil<MesChuXinNiuZhuanHanReport> util = new ExcelUtil<MesChuXinNiuZhuanHanReport>(MesChuXinNiuZhuanHanReport.class);
//        util.exportExcel(response, list, "楚鑫端子焊接机数据");
//    }
}