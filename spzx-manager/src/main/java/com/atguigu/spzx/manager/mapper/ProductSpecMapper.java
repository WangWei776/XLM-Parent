package com.atguigu.spzx.manager.mapper;

import com.atguigu.spzx.manager.service.ProductSpecService;
import com.atguigu.spzx.model.entity.product.ProductSpec;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface ProductSpecMapper {

    List<ProductSpec> findByPage();

    void save(ProductSpec productSpec);

    void updateById(ProductSpec productSpec);

    void deleteById(Long id);

    List<ProductSpec> findAll();
}
