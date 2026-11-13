package com.miracle.extra.controller;

import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.extra.domain.BarcodeBindRecord;
import com.miracle.extra.domain.enums.BarcodeType;
import com.miracle.extra.service.IBarcodeBindService;
import com.miracle.extra.service.IProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extra/barcode/bind")
public class BarcodeBindController extends BaseController {

    @Autowired
    private IBarcodeBindService barcodeBindService;

    @Autowired
    private IProductInfoService productInfoService;

    @GetMapping("/products")
    public AjaxResult getProductList() {
        return success(productInfoService.selectActiveProducts());
    }

    @GetMapping("/validate")
    public AjaxResult validateBarcode(
            @RequestParam("barcodeType") String barcodeType,
            @RequestParam("barcode") String barcode,
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "deviceNo", required = false) String deviceNo) {
        try {
            BarcodeType type = BarcodeType.fromCode(barcodeType);
            barcodeBindService.validateBarcode(type, barcode, productCode, deviceNo);
            //取消重复校验，业务会出现同一个产品码绑定多个扩展码的情况
            //barcodeBindService.validateDuplicate(type, barcode);
            return AjaxResult.success("校验通过");
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @Log(title = "双码绑定", businessType = BusinessType.INSERT)
    @PostMapping("/bind")
    public AjaxResult bind(@RequestBody BarcodeBindRecord.BindRequest request) {
        try {
            BarcodeBindRecord record = barcodeBindService.bind(request);
            return AjaxResult.success("绑定成功", record);
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public TableDataInfo list(BarcodeBindRecord record) {
        startPage();
        List<BarcodeBindRecord> list = barcodeBindService.selectBindRecordList(record);
        return getDataTable(list);
    }

    @GetMapping("/info")
    public AjaxResult getInfo(@RequestParam("bindUuid") String bindUuid) {
        return success(barcodeBindService.getByBindUuid(bindUuid));
    }

    @Log(title = "删除绑定记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(barcodeBindService.deleteBindRecordById(id));
    }
}