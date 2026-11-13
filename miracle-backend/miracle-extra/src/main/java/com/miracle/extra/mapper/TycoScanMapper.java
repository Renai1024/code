package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.TycoScan;
import org.apache.ibatis.annotations.*;
import java.util.List;

/** 新增：沿用当前SLAVE2业务数据源；SQL统一放入TycoScanMapper.xml，旧注解SQL已保存历史快照。 */
@Mapper
@DataSource(DataSourceType.SLAVE2)
public interface TycoScanMapper {
    /** 新增：在当前事务连接上设置计算列/筛选索引要求的SQL Server选项，不修改其他数据源配置。 */
    void prepareWriteSession();
    TycoScan.Batch find(@Param("id") String id);
    /** 新增：删除时锁定箱头，防止并发重复删除及删除/提交状态交错。 */
    TycoScan.Batch findForUpdate(@Param("id") String id);
    /** 新增：产品码全局唯一，箱码全局唯一，均只占用未删除记录。 */
    int count(@Param("code") String code);
    int countBox(@Param("boxCode") String boxCode);
    int insert(TycoScan.Batch batch);
    int insertProduct(@Param("id") String id, @Param("code") String code);
    List<TycoScan.Batch> list();
    List<String> details(@Param("id") String id);
    /** 新增：箱头和明细在同一业务事务中逻辑删除。 */
    int deleteProducts(@Param("id") String id);
    int deleteBatch(@Param("id") String id);
}
