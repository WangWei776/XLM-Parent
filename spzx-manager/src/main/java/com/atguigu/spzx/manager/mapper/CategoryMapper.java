package com.atguigu.spzx.manager.mapper;

import com.atguigu.spzx.model.entity.product.Category;
import com.atguigu.spzx.model.vo.product.CategoryExcelVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> findByParentId(Long parentId);

    int countByParentId(Long id);

    List<Category> findAll();


    void batchInert(List<CategoryExcelVo> categoryList);
}
