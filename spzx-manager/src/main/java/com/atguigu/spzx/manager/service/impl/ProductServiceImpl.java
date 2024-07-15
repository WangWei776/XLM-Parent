package com.atguigu.spzx.manager.service.impl;

import com.atguigu.spzx.manager.mapper.ProductDetailsMapper;
import com.atguigu.spzx.manager.mapper.ProductMapper;
import com.atguigu.spzx.manager.mapper.ProductSkuMapper;
import com.atguigu.spzx.manager.service.ProductService;
import com.atguigu.spzx.model.dto.product.ProductDto;
import com.atguigu.spzx.model.entity.product.ProductDetails;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atguigu.spzx.model.entity.product.Product;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private ProductDetailsMapper productDetailsMapper;

    @Override
    public PageInfo<Product> findByPage(Integer page, Integer limit, ProductDto productDto) {
        PageHelper.startPage(page, limit);
        List<Product> list = productMapper.findByPage(productDto);
        PageInfo<Product> pageInfo = new PageInfo<>(list);
        return pageInfo;

    }

    @Override
    @Transactional
    public void save(Product product) {
        product.setStatus(0);
        product.setAuditStatus(0);
        //保存商品信息
        productMapper.save(product);
        System.out.println(product.getId());
        //保存sku信息
        List<ProductSku> productSkuList = product.getProductSkuList();
        for (int i = 0; i < productSkuList.size(); i++) {
            ProductSku productSku = productSkuList.get(i);
            productSku.setSkuCode(product.getId() + "_" + i);
            productSku.setProductId(product.getId());
            productSku.setSkuName(product.getName() + productSku.getSkuSpec());
            productSku.setSaleNum(0);
            productSku.setStatus(0);
            productSkuMapper.save(productSku);
        }
        //保存details信息
        ProductDetails productDetails = new ProductDetails();
        productDetails.setProductId(product.getId());
        productDetails.setImageUrls(product.getDetailsImageUrls());
        productDetailsMapper.save(productDetails);
    }

    @Override
    public Product getById(Long id) {
        Product product = productMapper.getById(id);
        List <ProductSku> list = productSkuMapper.getById(id);
        ProductDetails productDetails = productDetailsMapper.getById(id);
        product.setProductSkuList(list);
        product.setDetailsImageUrls(productDetails.getImageUrls());
        return product;
    }

    @Override
    @Transactional
    public void updateById(Product product) {
        // 修改商品基本数据
        productMapper.updateById(product);

        // 修改商品的sku数据
        List<ProductSku> productSkuList = product.getProductSkuList();
        productSkuList.forEach(productSku -> {
            productSkuMapper.updateById(productSku);
        });

        // 修改商品的详情数据
        ProductDetails productDetails = productDetailsMapper.getById(product.getId());
        productDetails.setImageUrls(product.getDetailsImageUrls());
        productDetailsMapper.updateById(productDetails);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productMapper.deleteById(id);                   // 根据id删除商品基本数据
        productSkuMapper.deleteByProductId(id);         // 根据商品id删除商品的sku数据
        productDetailsMapper.deleteByProductId(id);     // 根据商品的id删除商品的详情数据
    }

    @Override
    public void updateAuditStatus(Long id, Integer auditStatus) {
        Product product = new Product();
        product.setId(id);
        if(auditStatus == 1) {
            product.setAuditStatus(1);
            product.setAuditMessage("审批通过");
        } else {
            product.setAuditStatus(-1);
            product.setAuditMessage("审批不通过");
        }
        productMapper.updateById(product);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        if(status == 1) {
            product.setStatus(1);
        } else {
            product.setStatus(-1);
        }
        productMapper.updateById(product);
    }
}
