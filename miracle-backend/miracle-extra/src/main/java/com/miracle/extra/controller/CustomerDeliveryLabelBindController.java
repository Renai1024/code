package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.extra.domain.BarcodeBindRecord;
import com.miracle.extra.service.ICustomerDeliveryLabelBindService;
import com.miracle.extra.service.ICustomerDeliveryLabelBindService.CustomerDeliveryBindRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 客户出货标签绑定 Controller
 * 业务流程：
 * 1. 选择客户
 * 2. 扫描客户外箱标签，反解析出客户料号
 * 3. 扫描出货标签条码，查询获取客户料号
 * 4. 校验两个客户料号一致后绑定到 BarcodeBind 表
 */
@RestController
@Anonymous
@RequestMapping("/extra/customer/deliveryLabelBind")
public class CustomerDeliveryLabelBindController extends BaseController {

    @Autowired
    private ICustomerDeliveryLabelBindService customerDeliveryLabelBindService;

    /**
     * 验证外箱标签码：根据客户规则的正则表达式反解析出各字段
     * 返回字段解析结果）
     */
    @GetMapping("/validateOuterBox")
    public AjaxResult validateOuterBox(@RequestParam("customerCode") String customerCode,
                                       @RequestParam("outerBoxBarcode") String outerBoxBarcode) {
        try {
            java.util.Map<String, Object> data = customerDeliveryLabelBindService.validateOuterBoxBarcode(customerCode, outerBoxBarcode);
            return AjaxResult.success(data);
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 验证出货标签条码：查询 slave4 (vwOutBoxLabelRecord) 获取客户料号
     */
    @GetMapping("/validateDeliveryLabel")
    public AjaxResult validateDeliveryLabel(@RequestParam("customerCode") String customerCode,
                                            @RequestParam("deliveryLabelBarcode") String deliveryLabelBarcode) {
        try {
            String customerPartNumber = customerDeliveryLabelBindService.validateDeliveryLabelBarcode(customerCode, deliveryLabelBarcode);
            AjaxResult result = AjaxResult.success();
            HashMap<String, Object> data = new HashMap<>();
            data.put("CustomerProductCode", customerPartNumber);
            result.put("data", data);
            return result;
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 绑定外箱标签与出货标签：校验两个客户料号一致后写入 BarcodeBind 表
     */
    @Log(title = "客户出货标签绑定", businessType = BusinessType.INSERT)
    @PostMapping("/bind")
    public AjaxResult bind(@RequestBody CustomerDeliveryBindRequest request) {
        try {
            BarcodeBindRecord record = customerDeliveryLabelBindService.bind(request);
            return AjaxResult.success("绑定成功", record);
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 查询绑定记录列表（自动过滤为客户出货标签绑定类型）
     */
    @GetMapping("/list")
    public TableDataInfo list(BarcodeBindRecord record) {
        startPage();
        List<BarcodeBindRecord> list = customerDeliveryLabelBindService.selectBindList(record);
        return getDataTable(list);
    }

    /**
     * 获取绑定记录详细
     */
    @GetMapping("/info")
    public AjaxResult getInfo(@RequestParam("bindUuid") String bindUuid) {
        return success(customerDeliveryLabelBindService.selectByBindUuid(bindUuid));
    }

//    /**
//     * 删除绑定记录（逻辑删除）
//     */
//    @Log(title = "客户出货标签绑定", businessType = BusinessType.DELETE)
//    @DeleteMapping("/{id}")
//    public AjaxResult remove(@PathVariable Long id) {
//        return toAjax(customerDeliveryLabelBindService.deleteById(id));
//    }
}
