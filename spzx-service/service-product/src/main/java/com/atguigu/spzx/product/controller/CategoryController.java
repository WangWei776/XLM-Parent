package com.atguigu.spzx.product.controller;


import com.atguigu.spzx.model.entity.product.Category;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/findCategoryTree")
    public Result findCategoryTree(){
        List<Category> categories = categoryService.findCategoryTree();
        return Result.build(categories, ResultCodeEnum.SUCCESS);
    }
}
