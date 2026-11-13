package com.miracle.extra.controller;

import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.extra.domain.TycoPacking.BatchUnpackCommand;
import com.miracle.extra.domain.TycoPacking.Command;
import com.miracle.extra.service.ITycoPackingService;
import com.miracle.extra.service.impl.TycoDraftService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;

/** 新增：双页签装箱入口，页面草稿与正式状态分离，解绑/拆箱独立授权。 */
@RestController
@RequestMapping("/extra/mes/report/mes_tyco_scan/packing")
public class TycoPackingController extends BaseController {
    private final ITycoPackingService service;
    private final TycoDraftService drafts;
    public TycoPackingController(ITycoPackingService service,TycoDraftService drafts) { this.service=service; this.drafts=drafts; }


    @GetMapping("/current") @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:add')")
    public AjaxResult current(@RequestParam String kind) { return success(service.current(kind)); }

    @GetMapping("/list") @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:list')")
    public TableDataInfo list(@RequestParam String kind) { startPage(); return getDataTable(service.list(kind)); }

    @GetMapping("/detail/{id}") @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:list')")
    public AjaxResult detail(@PathVariable String id) { return success(service.detail(id)); }

    @PostMapping("/{action:start|scan|remove|finish|bind|cancel}")
    @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:add')")
    @Log(title="泰科内外箱装箱",businessType=BusinessType.UPDATE)
    public AjaxResult operate(@PathVariable String action,@RequestBody Command command) { return run(action,command); }

    @PostMapping("/{action:unbind|unpack}")
    @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:remove')")
    @Log(title="泰科解绑拆箱",businessType=BusinessType.UPDATE)
    public AjaxResult release(@PathVariable String action,@RequestBody Command command) { return run(action,command); }

    /** 批量拆箱沿用拆箱权限和操作日志，整批进入同一个事务。 */
    @PostMapping("/unpack-batch")
    @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:remove')")
    @Log(title="泰科批量拆箱",businessType=BusinessType.UPDATE)
    public AjaxResult unpackBatch(@RequestBody BatchUnpackCommand command) {
        return run("unpack-batch",command);
    }

    private AjaxResult run(String action,Command command) {
        try { return success(service.execute(action,command)); }
        catch(IllegalArgumentException e) { return error(e.getMessage()); }
    }

    @GetMapping("/draft/{kind}") @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:add')")
    public AjaxResult draft(@PathVariable String kind) { return success(drafts.packingRead(kind)); }

    @PutMapping("/draft/{kind}") @PreAuthorize("@ss.hasPermi('extra:mes_tyco_scan:add')")
    public AjaxResult draftSave(@PathVariable String kind,@RequestBody JsonNode request) {
        try { return success(drafts.packingSave(kind,request)); }
        catch(IllegalArgumentException e) { return error(e.getMessage()); }
    }
}
