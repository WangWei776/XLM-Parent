package com.atguigu.spzx.product.service;

import com.atguigu.spzx.model.dto.h5.ProductSkuDto;
import com.atguigu.spzx.model.entity.product.ProductSku;

import java.util.List;

public interface ProductSkuService {
    List<ProductSku> findData();
}
