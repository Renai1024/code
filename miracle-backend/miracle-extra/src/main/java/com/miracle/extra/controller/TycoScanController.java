package com.miracle.extra.controller;

import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.extra.domain.TycoScan;
import com.miracle.extra.service.ITycoScanService;
import com.miracle.common.annotation.Log;
import com.miracle.common.enums.BusinessType;
import com.miracle.extra.service.impl.TycoDraftService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** 新增：独立鉴权入口，不开放匿名扫码或直接相信客户端数量。 */
@RestController
@RequestMapping("/extra/mes/report/mes_tyco_scan")
public class TycoScanController extends BaseController {
    private final ITycoScanService service;
    private final TycoDraftService drafts;
    // 新增：历史构造方法已随原控制器保存于implementation-history，草稿接口复用扫码权限。
    public TycoScanController(ITycoScanService service, TycoDraftService drafts) { this.service = service; this.drafts = drafts; }
    @GetMapping("/draft") @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:add')")
    public AjaxResult draft() { return success(drafts.read()); }
    @PutMapping("/draft") @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:add')")
    public AjaxResult saveDraft(@RequestBody JsonNode request) {
        try { return success(drafts.save(request)); }
        catch (IllegalArgumentException e) { return error(e.getMessage()); }
    }
    @DeleteMapping("/draft") @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:add')")
    public AjaxResult clearDraft(@RequestBody JsonNode request) {
        try { return success(drafts.clear(request)); }
        catch (IllegalArgumentException e) { return error(e.getMessage()); }
    }
    @GetMapping("/list") @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:list')")
    public TableDataInfo list() { startPage(); return getDataTable(service.list()); }
    @GetMapping("/{id}/products") @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:list')")
    public AjaxResult details(@PathVariable String id) { return success(service.details(id)); }
    @PostMapping("/parse") @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:add')")
    public AjaxResult parse(@RequestBody String boxCode) {
        // 新增：保留旧端点以明确拒绝旧客户端，防止绕过新版逐条SN占用和装箱核对。
        // 历史方法正文保存在implementation-history/20261103-tyco-inner-outer/TycoScanController.java。
        return error("旧版扫码已停用，请刷新页面使用内箱/外箱装箱页签");
    }
    @GetMapping("/used") @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:add')")
    // 新增：产品码独立全局校验；旧前端多传boxCode不影响兼容。
    public AjaxResult used(@RequestParam String code) {
        try { return success(service.used(code)); }
        catch (IllegalArgumentException e) { return error(e.getMessage()); }
    }
    @PostMapping @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:add')")
    @Log(title = "泰科产品扫码", businessType = BusinessType.INSERT)
    public AjaxResult submit(@RequestBody TycoScan.Submit request) {
        // 新增：历史实现完整保留，新提交统一走packing，避免新旧表并行写入突破唯一校验。
        return error("旧版提交已停用，请刷新页面使用内箱/外箱装箱页签");
    }

    /** 新增：按项目习惯提供受权限控制的删除接口，并写入系统操作日志。 */
    @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:remove')")
    @Log(title = "泰科产品扫码", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        try {
            int count = service.deleteByIds(ids);
            return success("删除成功，本次删除" + count + "箱记录");
        } catch (IllegalArgumentException e) { return error(e.getMessage()); }
    }
}
