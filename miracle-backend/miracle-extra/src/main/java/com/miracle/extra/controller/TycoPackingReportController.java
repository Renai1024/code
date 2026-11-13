package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import org.springframework.web.bind.annotation.*;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.extra.domain.TycoPackingReport.Query;
import com.miracle.extra.domain.TycoPackingReport.ExportRow;
import com.miracle.extra.service.ITycoPackingReportService;
import com.miracle.common.annotation.Log;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.utils.poi.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;

/** 展示报表支持匿名查询和导出，各接口显式开放。 */
@RestController
@RequestMapping("/extra/mes/report/mes_tyco_scan/packing/report")
// 历史：@PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:list')")，原文件保存在历史快照。
public class TycoPackingReportController extends BaseController {
    private final ITycoPackingReportService service;

    public TycoPackingReportController(ITycoPackingReportService service) {
        this.service = service;
    }

    @GetMapping("/list")
    @Anonymous
    public TableDataInfo list(Query query) {
        startPage();
        return getDataTable(service.list(query));
    }

    @GetMapping("/{id}")
    @Anonymous
    public AjaxResult detail(@PathVariable String id) {
        return success(service.detail(id));
    }

    /** 匿名导出沿用数量限制和筛选校验。 */
    @PostMapping("/export")
    @Anonymous
    @Log(title="泰科装箱追溯报表", businessType=BusinessType.EXPORT)
    public void export(HttpServletResponse response, Query query) {
        new ExcelUtil<>(ExportRow.class).exportExcel(response,service.export(query),"泰科装箱追溯报表");
    }
}
