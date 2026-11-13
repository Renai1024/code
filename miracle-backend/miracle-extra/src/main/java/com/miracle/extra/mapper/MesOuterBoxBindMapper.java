package com.miracle.extra.mapper;

import com.miracle.extra.domain.MesOuterBoxBindRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 外箱编码绑定产品编码Mapper接口
 *
 * @author miracle
 * @date 2026-04-10
 */
public interface MesOuterBoxBindMapper {

    /**
     * 根据外箱编码查询外箱绑定记录
     *
     * @param outerBoxBarcode 外箱编码
     * @return 外箱绑定记录
     */
    List<MesOuterBoxBindRecord> selectMesOuterBoxBindRecordByOuterBoxBarcode(String outerBoxBarcode);
    
    /**
     * 根据外箱编码查询绑定的产品编号列表
     *
     * @param outerBoxBarcode 外箱编码
     * @return 产品编号列表
     */
    List<String> selectProductsByOuterBoxBarcode(String outerBoxBarcode);

    /**
     * 根据产品编码查询外箱绑定记录
     *
     * @param productBarcode 产品编码
     * @return 外箱绑定记录
     */
    public List<MesOuterBoxBindRecord> selectMesOuterBoxBindRecordByProductBarcode(@Param("productBarcode") String productBarcode);

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
     * 查询所有不重复的外箱编码
     *
     * @param record 外箱绑定记录
     * @return 外箱编码列表
     */
    List<String> selectDistinctOuterBoxBarcodes(MesOuterBoxBindRecord record);

    /**
     * 新增外箱绑定记录
     *
     * @param record 外箱绑定记录
     * @return 结果
     */
    public int insertMesOuterBoxBindRecord(MesOuterBoxBindRecord record);

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
    public int deleteMesOuterBoxBindRecordByOuterBoxBarcode(@Param("outerBoxBarcode") String outerBoxBarcode);
}
