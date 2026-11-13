package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.ProductInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DataSource(value = DataSourceType.SLAVE6)
@Mapper
public interface ProductInfoMapper {
    
    ProductInfo selectProductByCode(@Param("productCode") String productCode);
    
    List<ProductInfo> selectProductList(ProductInfo productInfo);
    
    List<ProductInfo> selectActiveProducts();
}