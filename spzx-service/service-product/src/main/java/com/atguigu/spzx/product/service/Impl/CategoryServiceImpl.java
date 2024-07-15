package com.atguigu.spzx.product.service.Impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.atguigu.spzx.model.entity.product.Category;
import com.atguigu.spzx.product.mapper.CategoryMapper;
import com.atguigu.spzx.product.service.CategoryService;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<Category> findData() {
        List<Category> categories = categoryMapper.findData();
        return categories;
    }

    @Cacheable(value = "category", key = "'all'")
    @Override
    public List<Category> findCategoryTree() {
        //获取全部分类数据
        List<Category> categories = categoryMapper.findCategory();
        //全部一级分类
        List<Category> oneCategoryList = categories.stream().filter(item -> item.getParentId().longValue() == 0).collect(Collectors.toList());
        if (!oneCategoryList.isEmpty()){
            oneCategoryList.forEach(oneCategory ->{
                //全部二级分类
                List<Category> twoCategoryList = categories.stream().filter(
                        item -> item.getParentId().longValue() == oneCategory.getId().longValue())
                        .collect(Collectors.toList());
                oneCategory.setChildren(twoCategoryList);
                if (!twoCategoryList.isEmpty()){
                    twoCategoryList.forEach(twoCategory ->{
                        //全部三级分类
                        List<Category> threeCategoryList = categories.stream().filter(
                                        item -> item.getParentId().longValue() == twoCategory.getId().longValue())
                                .collect(Collectors.toList());
                        twoCategory.setChildren(threeCategoryList);
                    });
                }
            });
        }
        return oneCategoryList;
    }
}
