package com.atguigu.spzx.product.service.Impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.spzx.model.dto.h5.ProductSkuDto;
import com.atguigu.spzx.model.dto.product.SkuSaleDto;
import com.atguigu.spzx.model.entity.product.Product;
import com.atguigu.spzx.model.entity.product.ProductDetails;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.model.vo.h5.ProductItemVo;
import com.atguigu.spzx.product.mapper.ProductDetailsMapper;
import com.atguigu.spzx.product.mapper.ProductMapper;
import com.atguigu.spzx.product.mapper.ProductSkuMapper;
import com.atguigu.spzx.product.service.ProductService;
import com.atguigu.spzx.product.service.ProductSkuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductDetailsMapper productDetailsMapper;

    @Override
    public PageInfo<ProductSku> findByPage(Integer page, Integer limit, ProductSkuDto productSkuDto) {
        PageHelper.startPage(page, limit);
        List<ProductSku> productSkuPageInfo= productSkuMapper.findByPage(productSkuDto);
        PageInfo<ProductSku> pageInfo = new PageInfo<>(productSkuPageInfo);
        return pageInfo;
    }

    @Override
    public ProductItemVo item(Long skuId) {
        //获取尚品sku信息
        ProductSku productSku = productSkuMapper.getById(skuId);
        //商品信息
        //尚品轮播图
        Product product = productMapper.getProductById(productSku.getProductId());
        //同一个商品下面的sku信息列表
        List<ProductSku> productSkuList = productSkuMapper.findByProductId(productSku.getProductId());
        //建立sku规格与skuId对应关系
        Map<String,Object> skuSpecValueMap = new HashMap<>();
        productSkuList.forEach(item -> {
            skuSpecValueMap.put(item.getSkuSpec(), item.getId());
        });
        //商品详情图片
        ProductDetails productDetails = productDetailsMapper.getById(productSku.getProductId());

        ProductItemVo productItemVo = new ProductItemVo();

        productItemVo.setProductSku(productSku);
        productItemVo.setProduct(product);
        productItemVo.setDetailsImageUrlList(Arrays.asList(productDetails.getImageUrls().split(",")));
        productItemVo.setSpecValueList(JSON.parseArray(product.getSpecValue()));
        productItemVo.setSliderUrlList(Arrays.asList(product.getSliderUrls().split(",")));
        productItemVo.setSkuSpecValueMap(skuSpecValueMap);
        return productItemVo;
    }

    @Override
    public ProductSku getBySkuId(Long skuId) {
        return productSkuMapper.getById(skuId);
    }

    @Override
    public Boolean updateSkuSaleNum(List<SkuSaleDto> skuSaleDtoList) {
        if(!CollectionUtils.isEmpty(skuSaleDtoList)) {
            for(SkuSaleDto skuSaleDto : skuSaleDtoList) {
                productSkuMapper.updateSale(skuSaleDto.getSkuId(), skuSaleDto.getNum());
            }
        }
        return true;
    }
}
