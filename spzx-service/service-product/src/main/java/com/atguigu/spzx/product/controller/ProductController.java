package com.atguigu.spzx.product.controller;

import com.atguigu.spzx.model.dto.h5.ProductSkuDto;
import com.atguigu.spzx.model.dto.product.SkuSaleDto;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.h5.ProductItemVo;
import com.atguigu.spzx.product.service.ProductService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Retention;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("updateSkuSaleNum")
    public Boolean updateSkuSaleNum(@RequestBody List<SkuSaleDto> skuSaleDtoList) {
        return productService.updateSkuSaleNum(skuSaleDtoList);
    }

    @GetMapping("getBySkuId/{skuId}")
    public ProductSku getBySkuId(@Parameter(name = "skuId", description = "商品skuId", required = true) @PathVariable Long skuId) {
        ProductSku productSku = productService.getBySkuId(skuId);
        return productSku;
    }

    @GetMapping("item/{skuId}")
    public Result item(@Parameter(name = "skuId", description = "商品skuId", required = true)
                           @PathVariable Long skuId){
        ProductItemVo productItemVo = productService.item(skuId);
        return Result.build(productItemVo, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/{page}/{limit}")
    public Result findByPage(@PathVariable Integer page,
                             @PathVariable Integer limit,
                             ProductSkuDto productSkuDto){
        PageInfo<ProductSku> pageInfo = productService.findByPage(page, limit, productSkuDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }
}
