package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.CustomerOuterBoxRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户外箱标签解析规则 Mapper 接口
 *
 * @author miracle
 */
@DataSource(value = DataSourceType.MASTER)
public interface CustomerOuterBoxRuleMapper {

    /**
     * 根据ID查询
     */
    CustomerOuterBoxRule selectCustomerOuterBoxRuleById(Long id);

    /**
     * 根据客户编码查询该客户所有规则项（一个客户可配置多条字段解析规则）
     */
    List<CustomerOuterBoxRule> selectByCustomerCode(@Param("customerCode") String customerCode);

    /**
     * 查询列表
     */
    List<CustomerOuterBoxRule> selectCustomerOuterBoxRuleList(CustomerOuterBoxRule rule);

    /**
     * 查询所有启用的客户规则
     */
    List<CustomerOuterBoxRule> selectActiveRules();

    /**
     * 新增
     */
    int insertCustomerOuterBoxRule(CustomerOuterBoxRule rule);

    /**
     * 修改
     */
    int updateCustomerOuterBoxRule(CustomerOuterBoxRule rule);

    /**
     * 删除（逻辑删除）
     */
    int deleteCustomerOuterBoxRuleById(Long id);

    /**
     * 批量删除
     */
    int deleteCustomerOuterBoxRuleByIds(Long[] ids);
}
