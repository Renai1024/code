package com.miracle.extra.service.impl;

import com.miracle.common.utils.SecurityUtils;
import com.miracle.extra.domain.MesOuterBoxBindRecord;
import com.miracle.extra.mapper.MesOuterBoxBindMapper;
import com.miracle.extra.service.IMesOuterBoxBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 外箱编码绑定产品编码Service业务层处理
 *
 * @author liangzipeng
 * @date 2026-04-10
 */
@Service
public class MesOuterBoxBindServiceImpl implements IMesOuterBoxBindService {

    @Autowired
    private MesOuterBoxBindMapper outerBoxBindMapper;

    /**
     * 查询外箱绑定记录
     *
     * @param outerBoxBarcode 外箱编码
     * @return 外箱绑定记录
     */
    @Override
    public List<MesOuterBoxBindRecord> selectMesOuterBoxBindRecordByOuterBoxBarcode(String outerBoxBarcode) {
        return outerBoxBindMapper.selectMesOuterBoxBindRecordByOuterBoxBarcode(outerBoxBarcode);
    }
    
    @Override
    public List<String> selectProductsByOuterBoxBarcode(String outerBoxBarcode) {
        return outerBoxBindMapper.selectProductsByOuterBoxBarcode(outerBoxBarcode);
    }

    /**
     * 查询外箱绑定记录列表
     *
     * @param record 外箱绑定记录
     * @return 外箱绑定记录集合
     */
    @Override
    public List<MesOuterBoxBindRecord> selectMesOuterBoxBindRecordList(MesOuterBoxBindRecord record) {
        return outerBoxBindMapper.selectMesOuterBoxBindRecordList(record);
    }

    /**
     * 查询外箱绑定记录分组列表
     * 按外箱编码分组，返回每组的第一条记录
     *
     * @param record 外箱绑定记录
     * @return 外箱绑定记录分组集合
     */
    @Override
    public List<Map<String, Object>> selectMesOuterBoxBindRecordGroupList(MesOuterBoxBindRecord record) {
        return outerBoxBindMapper.selectMesOuterBoxBindRecordGroupList(record);
    }

    /**
     * 批量新增外箱绑定记录
     *
     * @param records 外箱绑定记录列表
     * @return 结果
     */
    @Override
    public int batchInsertMesOuterBoxBindRecord(List<MesOuterBoxBindRecord> records) {
        return outerBoxBindMapper.batchInsertMesOuterBoxBindRecord(records);
    }

    /**
     * 删除外箱绑定记录
     *
     * @param outerBoxBarcode 外箱编码
     * @return 结果
     */
    @Override
    public int deleteMesOuterBoxBindRecordByOuterBoxBarcode(String outerBoxBarcode) {
        return outerBoxBindMapper.deleteMesOuterBoxBindRecordByOuterBoxBarcode(outerBoxBarcode);
    }

    /**
     * 验证外箱编码
     * 提取客户料号(19~26位)和箱装数量(Q:xxx/格式)
     *
     * @param outerBoxBarcode 外箱编码
     * @return 验证结果
     */
    @Override
    public Object[] validateOuterBoxBarcode(String outerBoxBarcode) {
        if (outerBoxBarcode == null || outerBoxBarcode.length() < 26) {
            throw new IllegalArgumentException("外箱编码长度不足，无法提取客户料号");
        }

        // 提取客户料号(19~26位)
        String customerPartNumber;
        try {
            customerPartNumber = outerBoxBarcode.substring(18, 26);
        } catch (Exception e) {
            throw new IllegalArgumentException("外箱编码格式错误，无法提取客户料号");
        }

        // 提取箱装数量(Q:xxx/格式)
        Integer boxQuantity = extractBoxQuantity(outerBoxBarcode);
        if (boxQuantity == null) {
            throw new IllegalArgumentException("外箱编码格式错误，无法提取箱装数量，请确保编码包含Q:数量/格式");
        }

        // 检查外箱编码是否已绑定
        List<MesOuterBoxBindRecord> existingRecords = selectMesOuterBoxBindRecordByOuterBoxBarcode(outerBoxBarcode);
        if (existingRecords != null && !existingRecords.isEmpty()) {
            throw new IllegalArgumentException("该外箱编码已绑定产品，请勿重复绑定");
        }

        return new Object[]{customerPartNumber, boxQuantity};
    }

    /**
     * 验证产品编码
     * 提取客户料号(11~18位)
     *
     * @param productBarcode 产品编码
     * @return 客户料号
     */
    @Override
    public String validateProductBarcode(String productBarcode) {
        if (productBarcode == null || productBarcode.length() < 18) {
            throw new IllegalArgumentException("产品编码长度不足，无法提取客户料号");
        }

        // 提取客户料号(11~18位)
        String customerPartNumber;
        try {
            customerPartNumber = productBarcode.substring(10, 18);
        } catch (Exception e) {
            throw new IllegalArgumentException("产品编码格式错误，无法提取客户料号");
        }
        List<MesOuterBoxBindRecord> records =  outerBoxBindMapper.selectMesOuterBoxBindRecordByProductBarcode(productBarcode);
        if (records != null && !records.isEmpty()){
            throw new IllegalArgumentException("该产品编码" + productBarcode + "已与其它外箱编码绑定！");
        }

        return customerPartNumber;
    }

    /**
     * 外箱编码绑定产品编码
     *
     * @param request 绑定请求
     * @return 绑定结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int bindOuterBoxWithProducts(MesOuterBoxBindRecord.BindRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }

        String outerBoxBarcode = request.getOuterBoxBarcode();
        String[] productBarcodes = request.getProductBarcodes();

        if (outerBoxBarcode == null || outerBoxBarcode.trim().isEmpty()) {
            throw new IllegalArgumentException("外箱编码不能为空");
        }

        if (productBarcodes == null || productBarcodes.length == 0) {
            throw new IllegalArgumentException("产品编码列表不能为空");
        }

        // 验证外箱编码
        Object[] outerBoxValidation = validateOuterBoxBarcode(outerBoxBarcode);
        String outerBoxCustomerPartNumber = (String) outerBoxValidation[0];
        Integer boxQuantity = (Integer) outerBoxValidation[1];

        // 验证扫码数量
        int scanCount = productBarcodes.length;
        if (scanCount < boxQuantity) {
            throw new IllegalArgumentException("扫码数量不足，已扫码" + scanCount + "个，箱装数量为" + boxQuantity + "个，还差" + (boxQuantity - scanCount) + "个");
        }
        if (scanCount > boxQuantity) {
            throw new IllegalArgumentException("扫码数量超出，已扫码" + scanCount + "个，箱装数量为" + boxQuantity + "个，超出" + (scanCount - boxQuantity) + "个");
        }

        // 检查外箱编码是否已绑定
        List<MesOuterBoxBindRecord> existingRecords = selectMesOuterBoxBindRecordByOuterBoxBarcode(outerBoxBarcode);
        if (existingRecords != null && !existingRecords.isEmpty()) {
            throw new IllegalArgumentException("该外箱编码已绑定产品，请勿重复绑定");
        }

        // 收集错误信息
        List<String> errorMessages = new ArrayList<>();
        Set<String> productBarcodeSet = new HashSet<>();
        
        // 验证每个产品编码
        for (int i = 0; i < productBarcodes.length; i++) {
            String productBarcode = productBarcodes[i];
            
            // 检查产品编码是否重复
            if (!productBarcodeSet.add(productBarcode)) {
                errorMessages.add("第" + (i + 1) + "个产品编码重复：" + productBarcode);
                continue;
            }
            
            // 检查产品编码是否已绑定到其他外箱
            List<MesOuterBoxBindRecord> productRecords = outerBoxBindMapper.selectMesOuterBoxBindRecordByProductBarcode(productBarcode);
            if (productRecords != null && !productRecords.isEmpty()) {
                errorMessages.add("第" + (i + 1) + "个产品编码已绑定到其他外箱：" + productBarcode);
                continue;
            }
            
            try {
                // 验证产品编码格式和客户料号
                String productCustomerPartNumber = validateProductBarcode(productBarcode);
                if (!outerBoxCustomerPartNumber.equals(productCustomerPartNumber)) {
                    errorMessages.add("第" + (i + 1) + "个产品编码的客户料号不匹配。外箱编码客户料号：" + outerBoxCustomerPartNumber + "，产品编码客户料号：" + productCustomerPartNumber);
                }
            } catch (IllegalArgumentException e) {
                errorMessages.add("第" + (i + 1) + "个产品编码验证失败：" + e.getMessage());
            }
        }
        
        // 如果有错误，抛出所有错误信息
        if (!errorMessages.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("绑定失败，存在以下问题：");
            for (String msg : errorMessages) {
                errorMessage.append("\n").append(msg);
            }
            throw new IllegalArgumentException(errorMessage.toString());
        }

        // 执行绑定
        String bindBy = SecurityUtils.getUsername();
        Date bindTime = new Date();
        List<MesOuterBoxBindRecord> records = new ArrayList<>();

        for (int i = 0; i < productBarcodes.length; i++) {
            MesOuterBoxBindRecord record = new MesOuterBoxBindRecord();
            record.setUuid(UUID.randomUUID().toString());
            record.setOuterBoxBarcode(outerBoxBarcode);
            record.setProductBarcode(productBarcodes[i]);
            record.setBindSn((short) (i + 1));
            record.setBindTime(bindTime);
            record.setBindBy(bindBy);
            records.add(record);
        }

        return batchInsertMesOuterBoxBindRecord(records);
    }

    /**
     * 从外箱编码中提取箱装数量
     * 格式示例："...Q:100/..."
     * 提取"Q:"之后至"/"之前的字符作为箱装数量
     *
     * @param outerBoxBarcode 外箱编码
     * @return 箱装数量，如果提取失败返回null
     */
    private Integer extractBoxQuantity(String outerBoxBarcode) {
        if (outerBoxBarcode == null || outerBoxBarcode.isEmpty()) {
            return null;
        }

        // 使用正则表达式匹配 Q:数字/ 格式
        Pattern pattern = Pattern.compile("Q:(\\d+)/");
        Matcher matcher = pattern.matcher(outerBoxBarcode);

        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}
