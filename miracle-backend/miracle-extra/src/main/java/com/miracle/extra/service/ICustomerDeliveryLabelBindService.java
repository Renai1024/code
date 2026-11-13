package com.miracle.extra.service;

import com.miracle.extra.domain.BarcodeBindRecord;

import java.util.List;
import java.util.Map;

/**
 * 客户出货标签绑定 Service接口
 * 复用 BarcodeBind 表，通过 barcodeType 区分绑定类型：
 *   barcodeType1 = OUTER_BOX_CODE（客户外箱标签码），barcodeValue1 = 外箱标签完整码
 *   barcodeType2 = SERIAL_NUMBER（出货标签码），barcodeValue2 = 出货标签条码
 *   productCode = 校验一致后的客户料号
 *
 * @author miracle
 */
public interface ICustomerDeliveryLabelBindService {

    /**
     * 验证外箱标签码：根据客户规则的正则表达式反解析出各字段
     * 返回字段将作为客户料号用于校验
     *
     * @param customerCode 客户编码
     * @param outerBoxBarcode 外箱标签码
     * @return 解析结果，包含 customerPartNumber（校验字段值）和 fields（所有字段解析结果）
     */
    Map<String, Object> validateOuterBoxBarcode(String customerCode, String outerBoxBarcode);

    /**
     * 验证出货标签条码：查询 slave4 (vwOutBoxLabelRecord 视图) 获取客户料号
     *
     * @param customerCode 客户编码
     * @param deliveryLabelBarcode 出货标签条码
     * @return 客户料号
     */
    String validateDeliveryLabelBarcode(String customerCode, String deliveryLabelBarcode);

    /**
     * 绑定外箱标签与出货标签
     * 校验两个客户料号一致后，写入 BarcodeBindRecord 表
     *
     * @param request 绑定请求
     * @return 绑定记录
     */
    BarcodeBindRecord bind(CustomerDeliveryBindRequest request);

    /**
     * 查询客户出货标签绑定记录列表
     * 自动过滤 barcodeType1 = OUTER_BOX_CODE
     */
    List<BarcodeBindRecord> selectBindList(BarcodeBindRecord record);

    /**
     * 根据绑定UUID查询
     */
    BarcodeBindRecord selectByBindUuid(String bindUuid);

    /**
     * 删除绑定记录（逻辑删除）
     */
    int deleteById(Long id);

    /**
     * 绑定请求参数
     */
    class CustomerDeliveryBindRequest {
        private String customerCode;
        private String outerBoxBarcode;
        private String deliveryLabelBarcode;

        public String getCustomerCode() { return customerCode; }
        public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
        public String getOuterBoxBarcode() { return outerBoxBarcode; }
        public void setOuterBoxBarcode(String outerBoxBarcode) { this.outerBoxBarcode = outerBoxBarcode; }
        public String getDeliveryLabelBarcode() { return deliveryLabelBarcode; }
        public void setDeliveryLabelBarcode(String deliveryLabelBarcode) { this.deliveryLabelBarcode = deliveryLabelBarcode; }
    }
}
