package com.miracle.extra.service;

import com.miracle.extra.domain.CustomerOuterBoxRule;

import java.util.List;

/**
 * 客户外箱标签解析规则 Service接口
 *
 * @author miracle
 */
public interface ICustomerOuterBoxRuleService {

    CustomerOuterBoxRule selectById(Long id);

    /** 根据客户编码查询该客户所有规则项（一个客户可配置多条字段解析规则） */
    List<CustomerOuterBoxRule> selectByCustomerCode(String customerCode);

    List<CustomerOuterBoxRule> selectList(CustomerOuterBoxRule rule);

    List<CustomerOuterBoxRule> selectActiveRules();

    int insert(CustomerOuterBoxRule rule);

    int update(CustomerOuterBoxRule rule);

    int deleteById(Long id);

    int deleteByIds(Long[] ids);
}
