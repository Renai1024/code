package com.miracle.extra.service;

import com.miracle.common.core.domain.AjaxResult;
import com.miracle.extra.domain.MesOuterBoxBindRecord;

import java.util.List;
import java.util.Map;

/**
 * 外箱编码绑定产品编码Service接口
 *
 * @author liangzipeng
 * @date 2026-04-10
 */
public interface IMesOuterBoxBindService {

    /**
     * 根据外箱编码查询外箱绑定记录
     *
     * @param outerBoxBarcode 外箱编码
     * @return 外箱绑定记录
     */
    List<MesOuterBoxBindRecord> selectMesOuterBoxBindRecordByOuterBoxBarcode(String outerBoxBarcode);
    
    /**
     * 根据外箱编号查询绑定的产品编号列表
     *
     * @param outerBoxBarcode 外箱编码
     * @return 产品编号列表
     */
    List<String> selectProductsByOuterBoxBarcode(String outerBoxBarcode);

    /**
     * 查询外箱绑定记录列表
     *
     * @param record 外箱绑定记录
     * @return 外箱绑定记录集合
     */
    public List<MesOuterBoxBindRecord> selectMesOuterBoxBindRecordList(MesOuterBoxBindRecord record);

    /**
     * 查询外箱绑定记录分组列表
     * 按外箱编码分组，返回每组的汇总信息
     *
     * @param record 外箱绑定记录
     * @return 外箱绑定记录分组列表
     */
    List<Map<String, Object>> selectMesOuterBoxBindRecordGroupList(MesOuterBoxBindRecord record);

    /**
     * 批量新增外箱绑定记录
     *
     * @param records 外箱绑定记录列表
     * @return 结果
     */
    public int batchInsertMesOuterBoxBindRecord(List<MesOuterBoxBindRecord> records);

    /**
     * 删除外箱绑定记录
     *
     * @param outerBoxBarcode 外箱编码
     * @return 结果
     */
    public int deleteMesOuterBoxBindRecordByOuterBoxBarcode(String outerBoxBarcode);

    /**
     * 验证外箱编码
     * 提取客户料号(18~25位)和箱装数量(Q:xxx/格式)
     *
     * @param outerBoxBarcode 外箱编码
     * @return 包含客户料号和箱装数量的数组 [customerPartNumber, boxQuantity]
     */
    public Object[] validateOuterBoxBarcode(String outerBoxBarcode);

    /**
     * 验证产品编码
     * 提取客户料号(11~18位)
     *
     * @param productBarcode 产品编码
     * @return 客户料号
     */
    public String validateProductBarcode(String productBarcode);

    /**
     * 外箱编码绑定产品编码
     *
     * @param request 绑定请求
     * @return 绑定结果
     */
    public int bindOuterBoxWithProducts(MesOuterBoxBindRecord.BindRequest request);
}
