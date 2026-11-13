package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.FLACCheckH;
import com.miracle.extra.domain.WKACCheckH;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@DataSource(value = DataSourceType.SLAVE4)
@Mapper
public interface WKACCheckHMapper {

    /**
     * 查询WKACCheckH结果列表
     * @param wkacCheckH
     * @return
     */
    public List<WKACCheckH> selectWKACCheckHList(WKACCheckH wkacCheckH);
}
