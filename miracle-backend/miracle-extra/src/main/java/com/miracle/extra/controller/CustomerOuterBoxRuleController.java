package com.miracle.extra.controller;

import com.miracle.common.annotation.Log;
import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.domain.AjaxResult;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.extra.domain.CustomerOuterBoxRule;
import com.miracle.extra.service.ICustomerOuterBoxRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户外箱标签解析规则 Controller
 *
 * @author miracle
 */
@RestController
@Anonymous
@RequestMapping("/extra/customer/outerBoxRule")
public class CustomerOuterBoxRuleController extends BaseController {

    @Autowired
    private ICustomerOuterBoxRuleService customerOuterBoxRuleService;

    /**
     * 查询客户规则列表
     */
    @GetMapping("/list")
    public TableDataInfo list(CustomerOuterBoxRule rule) {
        startPage();
        List<CustomerOuterBoxRule> list = customerOuterBoxRuleService.selectList(rule);
        return getDataTable(list);
    }

    /**
     * 查询所有启用的客户规则（下拉选择用）
     */
    @GetMapping("/active")
    public AjaxResult getActiveRules() {
        return success(customerOuterBoxRuleService.selectActiveRules());
    }

    /**
     * 获取客户规则详细
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(customerOuterBoxRuleService.selectById(id));
    }

    /**
     * 新增客户规则
     */
    @Log(title = "客户外箱标签解析规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CustomerOuterBoxRule rule) {
        try {
            return toAjax(customerOuterBoxRuleService.insert(rule));
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 修改客户规则
     */
    @Log(title = "客户外箱标签解析规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CustomerOuterBoxRule rule) {
        try {
            return toAjax(customerOuterBoxRuleService.update(rule));
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

//    /**
//     * 删除客户规则
//     */
//    @Log(title = "客户外箱标签解析规则", businessType = BusinessType.DELETE)
//    @DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable Long[] ids) {
//        return toAjax(customerOuterBoxRuleService.deleteByIds(ids));
//    }
}
