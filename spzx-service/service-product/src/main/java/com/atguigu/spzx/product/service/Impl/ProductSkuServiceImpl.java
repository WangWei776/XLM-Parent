package com.atguigu.spzx.product.service.Impl;

import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.product.mapper.ProductSkuMapper;
import com.atguigu.spzx.product.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSkuServiceImpl implements ProductSkuService {

    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Override
    public List<ProductSku> findData() {
        List<ProductSku> productSkus = productSkuMapper.findData();
        return productSkus;
    }
}
