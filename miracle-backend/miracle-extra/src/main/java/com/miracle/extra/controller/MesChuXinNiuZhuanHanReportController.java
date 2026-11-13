package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesChuXinNiuZhuanHanReport;
import com.miracle.extra.service.IMesChuXinNiuZhuanHanReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Anonymous
@RequestMapping("/extra/mes/report/mes_report_chuXinHanJie")
public class MesChuXinNiuZhuanHanReportController extends BaseController {

    @Autowired
    private IMesChuXinNiuZhuanHanReportService mesChuXinNiuZhuanHanReportService;

    /**
     * 查询楚鑫扭转焊数据列表
     * @param mesChuXinNiuZhuanHanReport
     * @return
     */
    @GetMapping("/niuZhuan/list")
    public TableDataInfo niuZhuanList(MesChuXinNiuZhuanHanReport mesChuXinNiuZhuanHanReport){
        startPage();
        List<MesChuXinNiuZhuanHanReport> list = mesChuXinNiuZhuanHanReportService.selectMesChuXinNiuZhuanHanReportList(mesChuXinNiuZhuanHanReport);
        return getDataTable(list);
    }

    /**
     * 导出楚鑫扭转焊数据列表
     */
    @Log(title = "mesChuXinNiuZhuanHanReport", businessType = BusinessType.EXPORT)
    @PostMapping("/niuZhuan/export")
    public void mesChuXinNiuZhuanHanReportExport(HttpServletResponse response, MesChuXinNiuZhuanHanReport mesChuXinNiuZhuanHanReport)
    {
        List<MesChuXinNiuZhuanHanReport> list = mesChuXinNiuZhuanHanReportService.selectMesChuXinNiuZhuanHanReportList(mesChuXinNiuZhuanHanReport);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesChuXinNiuZhuanHanReport> util = new ExcelUtil<MesChuXinNiuZhuanHanReport>(MesChuXinNiuZhuanHanReport.class);
        util.exportExcel(response, list, "楚鑫扭转焊数据");
    }

    /**
     * 查询楚鑫超声波焊接数据列表
     * @param mesChuXinNiuZhuanHanReport
     * @return
     */
    @GetMapping("/chaoShengBo/list")
    public TableDataInfo chaoShengBoList(MesChuXinNiuZhuanHanReport mesChuXinNiuZhuanHanReport){
        startPage();
        List<MesChuXinNiuZhuanHanReport> list = mesChuXinNiuZhuanHanReportService.selectMesChuXinChaoShengBoHanReportList(mesChuXinNiuZhuanHanReport);
        return getDataTable(list);
    }

    /**
     * 导出楚鑫超声波焊数据列表
     */
    @Log(title = "mesChuXinChaoShengBoHanReport", businessType = BusinessType.EXPORT)
    @PostMapping("/chaoShengBo/export")
    public void mesChuXinChaoShengBoHanReportExport(HttpServletResponse response, MesChuXinNiuZhuanHanReport mesChuXinNiuZhuanHanReport)
    {
        List<MesChuXinNiuZhuanHanReport> list = mesChuXinNiuZhuanHanReportService.selectMesChuXinChaoShengBoHanReportList(mesChuXinNiuZhuanHanReport);
        if (list.size() > 50000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesChuXinNiuZhuanHanReport> util = new ExcelUtil<MesChuXinNiuZhuanHanReport>(MesChuXinNiuZhuanHanReport.class);
        util.exportExcel(response, list, "楚鑫超声波焊数据");
    }

    /**
     * 导出楚鑫端子焊数据列表
     */
    @Log(title = "mesChuXinChaoShengBoHanReport", businessType = BusinessType.EXPORT)
    @PostMapping("/duanZi/export")
    public void mesChuXinDuanZiHanReportExport(HttpServletResponse response, MesChuXinNiuZhuanHanReport mesChuXinNiuZhuanHanReport)
    {
        List<MesChuXinNiuZhuanHanReport> list = mesChuXinNiuZhuanHanReportService.selectMesChuXinDuanZiHanReportList(mesChuXinNiuZhuanHanReport);
        if (list.size() > 50000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesChuXinNiuZhuanHanReport> util = new ExcelUtil<MesChuXinNiuZhuanHanReport>(MesChuXinNiuZhuanHanReport.class);
        util.exportExcel(response, list, "楚鑫端子焊数据");
    }

    /**
     * 查询楚鑫端子焊接数据列表
     * @param mesChuXinNiuZhuanHanReport
     * @return
     */
    @GetMapping("/duanZi/list")
    public TableDataInfo duanZiList(MesChuXinNiuZhuanHanReport mesChuXinNiuZhuanHanReport){
        startPage();
        List<MesChuXinNiuZhuanHanReport> list = mesChuXinNiuZhuanHanReportService.selectMesChuXinDuanZiHanReportList(mesChuXinNiuZhuanHanReport);
        return getDataTable(list);
    }
}