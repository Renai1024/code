package com.miracle.extra.service;

import com.miracle.extra.domain.ProductInfo;

import java.util.List;

public interface IProductInfoService {
    
    ProductInfo getProductByCode(String productCode);
    
    List<ProductInfo> selectProductList(ProductInfo productInfo);
    
    List<ProductInfo> selectActiveProducts();
}