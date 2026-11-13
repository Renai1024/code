package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.extra.domain.MesOuterBoxBindRecord;
import com.miracle.extra.service.IMesOuterBoxBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import com.miracle.common.utils.poi.ExcelUtil;

/**
 * 外箱编码绑定产品编码Controller
 *
 * @author liangzipeng
 * @date 2026-04-10
 */
@RestController
@Anonymous
@RequestMapping("/extra/mes/outerBoxBind")
public class MesOuterBoxBindController extends BaseController {

    @Autowired
    private IMesOuterBoxBindService outerBoxBindService;

    /**
     * 查询外箱绑定记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(MesOuterBoxBindRecord record) {
        startPage();
        List<MesOuterBoxBindRecord> list = outerBoxBindService.selectMesOuterBoxBindRecordList(record);
        return getDataTable(list);
    }

    /**
     * 查询外箱绑定记录分组列表
     * 按外箱编码分组，返回每组的第一条记录
     */
    @GetMapping("/groupList")
    @PreAuthorize("@ss.hasPermi('extra:outer_box_bind:groupList')")
    public TableDataInfo groupList(MesOuterBoxBindRecord record) {
        startPage();
        List<Map<String, Object>> list = outerBoxBindService.selectMesOuterBoxBindRecordGroupList(record);
        return getDataTable(list);
    }

    /**
     * 获取外箱绑定记录详细信息
     */
    @GetMapping(value = "/info")
    @PreAuthorize("@ss.hasPermi('extra:outer_box_bind:groupList')")
    public AjaxResult getInfo(@RequestParam("outerBoxBarcode") String outerBoxBarcode) {
        return success(outerBoxBindService.selectMesOuterBoxBindRecordByOuterBoxBarcode(outerBoxBarcode));
    }
    
    /**
     * 根据外箱编号查询绑定的产品编号列表
     */
    @GetMapping(value = "/products")
    @PreAuthorize("@ss.hasPermi('extra:outer_box_bind:groupList')")
    public AjaxResult getProductsByOuterBoxBarcode(@RequestParam("outerBoxBarcode") String outerBoxBarcode) {
        return success(outerBoxBindService.selectProductsByOuterBoxBarcode(outerBoxBarcode));
    }
    
    /**
     * 导出外箱绑定记录
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, MesOuterBoxBindRecord record) {
        List<MesOuterBoxBindRecord> list = outerBoxBindService.selectMesOuterBoxBindRecordList(record);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesOuterBoxBindRecord> util = new ExcelUtil<MesOuterBoxBindRecord>(MesOuterBoxBindRecord.class);
        util.exportExcel(response, list, "外箱绑定记录");
    }

    /**
     * 验证外箱编码
     * 返回客户料号和箱装数量
     */
    @GetMapping("/validateOuterBox")
    @PreAuthorize("@ss.hasPermi('extra:outer_box_bind:validateOuterBox')")
    public AjaxResult validateOuterBox(@RequestParam("outerBoxBarcode") String outerBoxBarcode) {
        try {
            Object[] result = outerBoxBindService.validateOuterBoxBarcode(outerBoxBarcode);
            AjaxResult successResult = AjaxResult.success();
            HashMap<String,Object> data = new HashMap<>();
            data.put("customerPartNumber", result[0]);
            data.put("boxQuantity", result[1]);
            successResult.put("data", data);
            return successResult;
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 验证产品编码
     * 返回客户料号
     */
    @GetMapping("/validateProduct")
    @PreAuthorize("@ss.hasPermi('extra:outer_box_bind:validateProduct')")
    public AjaxResult validateProduct(@RequestParam("productBarcode") String productBarcode) {
        try {
            String customerPartNumber = outerBoxBindService.validateProductBarcode(productBarcode);
            AjaxResult successResult = AjaxResult.success();
            HashMap<String,Object> data = new HashMap<>();
            data.put("customerPartNumber", customerPartNumber);
            successResult.put("data", data);
            return successResult;
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 新增外箱编码绑定产品编码
     */
    @Log(title = "外箱编码绑定产品编码", businessType = BusinessType.INSERT)
    @PostMapping("/bind")
    @PreAuthorize("@ss.hasPermi('extra:outer_box_bind:bind')")
    public AjaxResult bind(@RequestBody MesOuterBoxBindRecord.BindRequest request) {
        try {
            // 重新验证外箱编码以获取客户料号和箱装数量
            Object[] validationResult = outerBoxBindService.validateOuterBoxBarcode(request.getOuterBoxBarcode());
            int result = outerBoxBindService.bindOuterBoxWithProducts(request);
            AjaxResult successResult = AjaxResult.success("绑定成功，共绑定" + result + "个产品编码");
            successResult.put("bindCount", result);
            successResult.put("customerPartNumber", validationResult[0]);
            successResult.put("boxQuantity", validationResult[1]);
            return successResult;
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 删除外箱绑定记录
     */
    @Log(title = "删除外箱编码绑定记录", businessType = BusinessType.DELETE)
    @PostMapping("delete")
    @PreAuthorize("@ss.hasPermi('extra:outer_box_bind:delete')")
    public AjaxResult remove(@RequestParam String outerBoxBarcode) {
        if (outerBoxBarcode.isEmpty()){
            return  AjaxResult.error("删除的外箱编码不能为空");
        }
        return toAjax(outerBoxBindService.deleteMesOuterBoxBindRecordByOuterBoxBarcode(outerBoxBarcode));
    }
}
