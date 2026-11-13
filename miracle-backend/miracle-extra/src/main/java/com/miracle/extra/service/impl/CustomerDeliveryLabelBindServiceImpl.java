package com.miracle.extra.service.impl;

import com.miracle.common.enums.DataSourceType;
import com.miracle.common.utils.SecurityUtils;
import com.miracle.extra.domain.BarcodeBindRecord;
import com.miracle.extra.domain.CustomerOuterBoxRule;
import com.miracle.extra.domain.DeliveryLabelWithoutSN;
import com.miracle.extra.domain.enums.BarcodeType;
import com.miracle.extra.mapper.BarcodeBindMapper;
import com.miracle.extra.mapper.CustomerOuterBoxRuleMapper;
import com.miracle.extra.mapper.DeliveryLabelWithoutSNMapper;
import com.miracle.extra.service.ICustomerDeliveryLabelBindService;
import com.miracle.framework.datasource.DynamicDataSourceContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 客户出货标签绑定 Service实现
 *
 * 业务流程：
 * 1. 选择客户（一个客户可配置多条字段解析规则，通过 customerCode 关联）
 * 2. 扫描客户外箱标签，按规则列表逐条用正则反解析出各业务字段值
 * 3. 扫描出货标签条码， 获取出货标签对应的客户料号
 * 4. 校验两个客户料号是否一致，一致则绑定，不一致弹窗报警
 *
 */
@Service
public class CustomerDeliveryLabelBindServiceImpl implements ICustomerDeliveryLabelBindService {

    @Autowired
    private CustomerOuterBoxRuleMapper customerOuterBoxRuleMapper;

    @Autowired
    private DeliveryLabelWithoutSNMapper deliveryLabelWithoutSNMapper;

    @Autowired
    private BarcodeBindMapper barcodeBindMapper;

    /** 校验字段约定名：fieldName 为此值的规则项解析结果作为客户料号 */
    private static final String FIELD_NAME_CUSTOMER_PART_NUMBER = "CustomerProductCode";

    /**
     * 验证外箱标签码：根据客户规则的正则表达式反解析出各字段
     */
    @Override
    public Map<String, Object> validateOuterBoxBarcode(String customerCode, String outerBoxBarcode) {
        if (customerCode == null || customerCode.trim().isEmpty()) {
            throw new IllegalArgumentException("客户编码不能为空");
        }
        if (outerBoxBarcode == null || outerBoxBarcode.trim().isEmpty()) {
            throw new IllegalArgumentException("外箱标签码不能为空");
        }

        // 查询该客户的所有规则项（主库）
        List<CustomerOuterBoxRule> rules = customerOuterBoxRuleMapper.selectByCustomerCode(customerCode);
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("未找到客户[" + customerCode + "]的解析规则配置");
        }

        // 逐条规则解析字段
        Map<String, Object> fields = new LinkedHashMap<>();
        String validateFieldValue = null;
        CustomerOuterBoxRule validateRule = null;

        for (CustomerOuterBoxRule rule : rules) {
            if (rule.getLabelRegex() == null || rule.getLabelRegex().trim().isEmpty()) {
                continue;
            }
            String value = extractByRegex(outerBoxBarcode, rule.getLabelRegex(), rule.getExtractGroup());
            Map<String, Object> fieldInfo = new HashMap<>();
            fieldInfo.put("fieldName", rule.getFieldName());
            fieldInfo.put("fieldLabel", rule.getFieldLabel());
            fieldInfo.put("value", value);
            if (rule.getFieldName() != null) {
                fields.put(rule.getFieldName(), fieldInfo);
            }

            // 通过 fieldName 约定识别校验字段（客户料号）
            if (rule.getFieldName() != null
                    && FIELD_NAME_CUSTOMER_PART_NUMBER.equals(rule.getFieldName())) {
                validateFieldValue = value;
                validateRule = rule;
            }
        }

        if (validateFieldValue == null || validateFieldValue.isEmpty()) {
            throw new IllegalArgumentException("外箱标签码无法解析出校验字段（客户料号），"
                    + "请检查规则中是否配置了 fieldName = " + FIELD_NAME_CUSTOMER_PART_NUMBER + " 的规则项");
        }

        // 检查外箱标签是否已绑定（SLAVE6 数据源，类型为 CUSTOMER_OUTER_BOX）
        List<BarcodeBindRecord> existing = doInDataSource(DataSourceType.SLAVE6, () ->
                barcodeBindMapper.selectByBarcode(BarcodeType.CUSTOMER_OUTER_BOX.getCode(), outerBoxBarcode));
        if (existing != null && !existing.isEmpty()) {
            throw new IllegalArgumentException("该外箱标签码已绑定出货标签，请勿重复绑定");
        }

        Map<String, Object> result = new HashMap<>();
        result.put(FIELD_NAME_CUSTOMER_PART_NUMBER, validateFieldValue);
        result.put("validateFieldLabel", validateRule != null ? validateRule.getFieldLabel() : "客户料号");
        result.put("fields", fields);
        return result;
    }

    /**
     * 验证出货标签条码：查询 slave4 (vwOutBoxLabelRecord) 获取客户料号
     */
    @Override
    public String validateDeliveryLabelBarcode(String customerCode, String deliveryLabelBarcode) {
        if (customerCode == null || customerCode.trim().isEmpty()) {
            throw new IllegalArgumentException("客户编码不能为空");
        }
        if (deliveryLabelBarcode == null || deliveryLabelBarcode.trim().isEmpty()) {
            throw new IllegalArgumentException("出货标签条码不能为空");
        }

        // 查询客户规则获取 customerFlag（主库）
        List<CustomerOuterBoxRule> rules = customerOuterBoxRuleMapper.selectByCustomerCode(customerCode);
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("未找到客户[" + customerCode + "]的解析规则配置");
        }
        CustomerOuterBoxRule firstRule = rules.get(0);
        String ruleCustomerFlag = firstRule.getCustomerFlag();

        // 查询 slave4 的 vwOutBoxLabelRecord 视图
        DeliveryLabelWithoutSN deliveryLabel = doInDataSource(DataSourceType.SLAVE4, () ->
                deliveryLabelWithoutSNMapper.selectByBarCodeStr(deliveryLabelBarcode));
        if (deliveryLabel == null) {
            throw new IllegalArgumentException("出货标签条码不存在：" + deliveryLabelBarcode);
        }

        // 视图已过滤 CustomerFlag = 'BYDOutBox'，这里仍校验客户标识配置一致性
        if (ruleCustomerFlag != null && !ruleCustomerFlag.isEmpty()
                && !ruleCustomerFlag.equals(deliveryLabel.getCustomerFlag())) {
            throw new IllegalArgumentException("出货标签的客户标识不匹配，期望[" + ruleCustomerFlag
                    + "]，实际[" + deliveryLabel.getCustomerFlag() + "]");
        }

        String customerPartNumber = deliveryLabel.getCustomerCode();
        if (customerPartNumber == null || customerPartNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("出货标签条码对应的客户料号为空：" + deliveryLabelBarcode);
        }

        // 检查出货标签是否已绑定（SLAVE6 数据源，类型为 SERIAL_NUMBER）
        List<BarcodeBindRecord> existing = doInDataSource(DataSourceType.SLAVE6, () ->
                barcodeBindMapper.selectByBarcode(BarcodeType.SERIAL_NUMBER.getCode(), deliveryLabelBarcode));
        if (existing != null && !existing.isEmpty()) {
            throw new IllegalArgumentException("该出货标签条码已绑定外箱标签，请勿重复绑定");
        }

        return customerPartNumber;
    }

    /**
     * 绑定外箱标签与出货标签
     * 校验两个客户料号一致后写入 BarcodeBind 表（SLAVE6 数据源）
     *
     * 注意：跨数据源操作（主库查规则 / SLAVE4 查视图 / SLAVE6 写绑定表），
     * 不使用 @Transactional（单库事务无法覆盖跨库操作），
     * 通过编程式数据源切换确保各 Mapper 访问正确的库。
     */
    @Override
    public BarcodeBindRecord bind(CustomerDeliveryBindRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getCustomerCode() == null || request.getCustomerCode().trim().isEmpty()) {
            throw new IllegalArgumentException("客户编码不能为空");
        }
        if (request.getOuterBoxBarcode() == null || request.getOuterBoxBarcode().trim().isEmpty()) {
            throw new IllegalArgumentException("外箱标签码不能为空");
        }
        if (request.getDeliveryLabelBarcode() == null || request.getDeliveryLabelBarcode().trim().isEmpty()) {
            throw new IllegalArgumentException("出货标签条码不能为空");
        }

        // 验证外箱标签码，获取校验字段值（客户料号）
        Map<String, Object> outerBoxResult = validateOuterBoxBarcode(request.getCustomerCode(), request.getOuterBoxBarcode());
        String outerBoxCustomerPartNumber = (String) outerBoxResult.get(FIELD_NAME_CUSTOMER_PART_NUMBER);

        // 验证出货标签条码，获取客户料号
        String deliveryLabelCustomerPartNumber = validateDeliveryLabelBarcode(request.getCustomerCode(), request.getDeliveryLabelBarcode());

        // 校验两个客户料号是否一致
        if (!outerBoxCustomerPartNumber.equals(deliveryLabelCustomerPartNumber)) {
            throw new IllegalArgumentException("客户料号不一致！外箱标签客户料号：" + outerBoxCustomerPartNumber
                    + "，出货标签客户料号：" + deliveryLabelCustomerPartNumber);
        }

        // 查询客户名称用于备注（主库）
        List<CustomerOuterBoxRule> rules = customerOuterBoxRuleMapper.selectByCustomerCode(request.getCustomerCode());
        String customerName = rules.isEmpty() ? request.getCustomerCode() : rules.get(0).getCustomerName();

        // 写入 BarcodeBind 表（SLAVE6 数据源）
        String bindUuid = UUID.randomUUID().toString().replace("-", "");
        String bindBy = SecurityUtils.getUsername();
        Date bindTime = new Date();

        BarcodeBindRecord record = new BarcodeBindRecord();
        record.setBindUuid(bindUuid);
        record.setProductCode(outerBoxCustomerPartNumber);
        record.setBarcodeType1(BarcodeType.CUSTOMER_OUTER_BOX.getCode());
        record.setBarcodeValue1(request.getOuterBoxBarcode());
        record.setBarcodeType2(BarcodeType.SERIAL_NUMBER.getCode());
        record.setBarcodeValue2(request.getDeliveryLabelBarcode());
        // 复用 deviceNo 字段存储客户编码，便于按客户筛选记录
        record.setDeviceNo(request.getCustomerCode());
        record.setRemark("客户出货标签绑定-" + customerName);
        record.setCreateTime(bindTime);
        record.setCreateBy(bindBy);
        record.setDeleted(false);

        doInDataSource(DataSourceType.SLAVE6, () -> {
            barcodeBindMapper.insertBarcodeBindRecord(record);
            return null;
        });

        return record;
    }

    @Override
    public List<BarcodeBindRecord> selectBindList(BarcodeBindRecord record) {
        // 强制过滤为客户出货标签绑定类型（barcodeType1 = CUSTOMER_OUTER_BOX）
        record.setBarcodeType1(BarcodeType.CUSTOMER_OUTER_BOX.getCode());
        return doInDataSource(DataSourceType.SLAVE6, () ->
                barcodeBindMapper.selectBarcodeBindRecordList(record));
    }

    @Override
    public BarcodeBindRecord selectByBindUuid(String bindUuid) {
        return doInDataSource(DataSourceType.SLAVE6, () ->
                barcodeBindMapper.selectByBindUuid(bindUuid));
    }

    @Override
    public int deleteById(Long id) {
        return doInDataSource(DataSourceType.SLAVE6, () ->
                barcodeBindMapper.deleteBarcodeBindRecordById(id));
    }

    /**
     * 在指定数据源下执行操作（用于跨数据源场景）
     * 由于 MyBatis Mapper 的 JDK 动态代理无法被 Spring AOP 的 @within 切面拦截，
     * Mapper 接口上的 @DataSource 注解不生效，需通过此方法编程式切换数据源。
     */
    private <T> T doInDataSource(DataSourceType dsType, Supplier<T> action) {
        DynamicDataSourceContextHolder.setDataSourceType(dsType.name());
        try {
            return action.get();
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 使用正则表达式从文本中提取内容
     */
    private String extractByRegex(String text, String regex, Integer group) {
        if (group == null || group < 1) {
            group = 1;
        }
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                if (matcher.groupCount() >= group) {
                    return matcher.group(group);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("正则表达式解析失败：" + e.getMessage());
        }
        return null;
    }
}
