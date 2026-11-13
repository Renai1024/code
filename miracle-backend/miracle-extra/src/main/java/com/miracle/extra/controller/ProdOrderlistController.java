package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.extra.domain.ProdOrderlist;
import com.miracle.extra.service.ProdOrderlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 生产工单 Controller
 */
@RestController
@RequestMapping("/api/prod-order-list")
public class ProdOrderlistController extends BaseController {

    @Autowired
    private ProdOrderlistService prodOrderlistService;

    /**
     * 根据工单号查询（不包含创建时间和图片）
     */
    @GetMapping("/{productionOrderNo}")
    public AjaxResult getByOrderNo(@PathVariable("productionOrderNo") String productionOrderNo) {
        ProdOrderlist prodOrder = prodOrderlistService.getByOrderNo(productionOrderNo);
        return success(prodOrder);
    }

    /**
     * 分页列表查询（不包含创建时间和图片）
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('extra:mes_picture_upload:list')")
    public TableDataInfo list(@RequestParam(required = false) String productionOrderNo,
                              @RequestParam(required = false) String materialCode,
                              @RequestParam(required = false) String materialName) {
        startPage();
        List<ProdOrderlist> list = prodOrderlistService.list(productionOrderNo, materialCode, materialName);
        return getDataTable(list);
    }

    /**
     * 新增（创建时间和图片由前端传入）
     */
    @PostMapping("/add")
    @PreAuthorize("@ss.hasPermi('mes_picture:add')")
    public AjaxResult add(@RequestBody ProdOrderlist prodOrder) {
        if (prodOrder.getProductionOrderNo() == null || prodOrder.getProductionOrderNo().isEmpty()) {
            return error("生产工单号不能为空");
        }
        int result = prodOrderlistService.insert(prodOrder);
        return result > 0 ? success() : error("新增失败");
    }

    /**
     * 批量新增
     */
    @PostMapping("/addBatch")
    @PreAuthorize("@ss.hasPermi('mes_picture:add')")
    public AjaxResult addBatch(@RequestBody List<ProdOrderlist> list) {
        if (list == null || list.isEmpty()) {
            return error("数据不能为空");
        }
        int result = prodOrderlistService.insertBatch(list);
        return result > 0 ? success() : error("批量新增失败");
    }

    /**
     * 修改（只修改车间和计划数量）
     */
    @PutMapping("/edit")
    @PreAuthorize("@ss.hasPermi('mes_picture:update')")
    public AjaxResult edit(@RequestBody ProdOrderlist prodOrder) {
        if (prodOrder.getProductionOrderNo() == null || prodOrder.getProductionOrderNo().isEmpty()) {
            return error("生产工单号不能为空");
        }
        int result = prodOrderlistService.update(prodOrder);
        return result > 0 ? success() : error("修改失败");
    }

    /**
     * 删除
     */
    @DeleteMapping("/{productionOrderNo}")
    @PreAuthorize("@ss.hasPermi('mes_picture:delete')")
    public AjaxResult delete(@PathVariable("productionOrderNo") String productionOrderNo) {
        int result = prodOrderlistService.delete(productionOrderNo);
        return result > 0 ? success() : error("删除失败");
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/deleteBatch")
    @PreAuthorize("@ss.hasPermi('mes_picture:delete')")
    public AjaxResult deleteBatch(@RequestBody List<String> orderNoList) {
        if (orderNoList == null || orderNoList.isEmpty()) {
            return error("工单号列表不能为空");
        }
        int result = prodOrderlistService.deleteBatch(orderNoList);
        return result > 0 ? success() : error("批量删除失败");
    }
}