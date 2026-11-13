package com.miracle.extra.service.impl;

import com.miracle.extra.domain.ProductInfo;
import com.miracle.extra.mapper.ProductInfoMapper;
import com.miracle.extra.service.IProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductInfoServiceImpl implements IProductInfoService {

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Override
    public ProductInfo getProductByCode(String productCode) {
        return productInfoMapper.selectProductByCode(productCode);
    }

    @Override
    public List<ProductInfo> selectProductList(ProductInfo productInfo) {
        return productInfoMapper.selectProductList(productInfo);
    }

    @Override
    public List<ProductInfo> selectActiveProducts() {
        return productInfoMapper.selectActiveProducts();
    }
}