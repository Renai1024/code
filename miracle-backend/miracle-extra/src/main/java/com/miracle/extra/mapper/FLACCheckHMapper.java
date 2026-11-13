package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.FLACCheckH;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@DataSource(value = DataSourceType.SLAVE4)
@Mapper
public interface FLACCheckHMapper {

    /**
     * 查询FLACCheckH结果列表
     * @param flacCheckH
     * @return
     */
    public List<FLACCheckH> selectFLACCheckHList(FLACCheckH flacCheckH);

    /**
     *  查询万肯气密性测试结果列表
     * @param flacCheckH
     * @return
     */
    public List<FLACCheckH> selectWKCheckPrintList(FLACCheckH flacCheckH);
}
