package com.miracle.extra.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.TycoPackingReport.*;

/** 报表使用与扫码业务相同的数据库，明细按需加载。 */
@Mapper
@DataSource(DataSourceType.SLAVE2)
public interface TycoPackingReportMapper {
    List<Row> list(Query query);
    Row box(@Param("id") String id);
    List<Sn> scans(@Param("id") String id);
    List<Binding> bindings(@Param("id") String id);
}
