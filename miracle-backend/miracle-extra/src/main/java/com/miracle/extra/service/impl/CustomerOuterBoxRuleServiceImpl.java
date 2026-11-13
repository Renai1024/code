package com.miracle.extra.service.impl;

import com.miracle.common.utils.SecurityUtils;
import com.miracle.extra.domain.CustomerOuterBoxRule;
import com.miracle.extra.mapper.CustomerOuterBoxRuleMapper;
import com.miracle.extra.service.ICustomerOuterBoxRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 客户外箱标签解析规则 Service实现
 *
 * @author miracle
 */
@Service
public class CustomerOuterBoxRuleServiceImpl implements ICustomerOuterBoxRuleService {

    @Autowired
    private CustomerOuterBoxRuleMapper customerOuterBoxRuleMapper;

    @Override
    public CustomerOuterBoxRule selectById(Long id) {
        return customerOuterBoxRuleMapper.selectCustomerOuterBoxRuleById(id);
    }

    @Override
    public List<CustomerOuterBoxRule> selectByCustomerCode(String customerCode) {
        return customerOuterBoxRuleMapper.selectByCustomerCode(customerCode);
    }

    @Override
    public List<CustomerOuterBoxRule> selectList(CustomerOuterBoxRule rule) {
        return customerOuterBoxRuleMapper.selectCustomerOuterBoxRuleList(rule);
    }

    @Override
    public List<CustomerOuterBoxRule> selectActiveRules() {
        return customerOuterBoxRuleMapper.selectActiveRules();
    }

    @Override
    public int insert(CustomerOuterBoxRule rule) {
        // 同一客户同一业务字段名不允许重复
        if (rule.getCustomerCode() != null && rule.getFieldName() != null) {
            List<CustomerOuterBoxRule> existing = customerOuterBoxRuleMapper.selectByCustomerCode(rule.getCustomerCode());
            if (existing != null && existing.stream().anyMatch(r -> rule.getFieldName().equals(r.getFieldName()))) {
                throw new IllegalArgumentException("客户[" + rule.getCustomerCode() + "]已配置字段[" + rule.getFieldName() + "]，请勿重复添加");
            }
        }
        if (rule.getExtractGroup() == null) {
            rule.setExtractGroup(1);
        }
        if (rule.getSortOrder() == null) {
            rule.setSortOrder(0);
        }
        if (rule.getActiveStatus() == null) {
            rule.setActiveStatus(1);
        }
        rule.setDeleted(false);
        rule.setCreateTime(new Date());
        rule.setCreateBy(SecurityUtils.getUsername());
        return customerOuterBoxRuleMapper.insertCustomerOuterBoxRule(rule);
    }

    @Override
    public int update(CustomerOuterBoxRule rule) {
        rule.setModifyTime(new Date());
        rule.setModifyBy(SecurityUtils.getUsername());
        return customerOuterBoxRuleMapper.updateCustomerOuterBoxRule(rule);
    }

    @Override
    public int deleteById(Long id) {
        return customerOuterBoxRuleMapper.deleteCustomerOuterBoxRuleById(id);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return customerOuterBoxRuleMapper.deleteCustomerOuterBoxRuleByIds(ids);
    }
}
