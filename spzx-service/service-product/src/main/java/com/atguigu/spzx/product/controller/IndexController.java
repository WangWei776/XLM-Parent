package com.atguigu.spzx.product.controller;


import com.atguigu.spzx.model.entity.product.Category;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.h5.IndexVo;
import com.atguigu.spzx.product.service.CategoryService;
import com.atguigu.spzx.product.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/product/index")
public class IndexController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductSkuService productSkuService;

    @GetMapping
    public Result findData(){
        List<Category> categories = categoryService.findData();
        List<ProductSku> productSkus = productSkuService.findData();
        IndexVo indexVo = new IndexVo();
        indexVo.setCategoryList(categories);
        indexVo.setProductSkuList(productSkus);
        return Result.build(indexVo, ResultCodeEnum.SUCCESS);

    }
}
