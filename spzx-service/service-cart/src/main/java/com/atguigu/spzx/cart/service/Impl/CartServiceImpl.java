package com.atguigu.spzx.cart.service.Impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.spzx.cart.service.CartService;
import com.atguigu.spzx.feign.product.ProductFeignClient;
import com.atguigu.spzx.model.entity.h5.CartInfo;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.model.entity.user.UserInfo;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.utils.AuthContextUtil;
import com.mysql.cj.xdevapi.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate<String , String> redisTemplate;
    @Autowired
    private ProductFeignClient productFeignClient;


    @Override
    public void allCheckCart(Integer isChecked) {
        Long userId = AuthContextUtil.getUserInfo().getId();
        String cartKey = getCartKey(userId);
        List<Object> cartInfoList = redisTemplate.opsForHash().values(cartKey);
        if (!CollectionUtils.isEmpty(cartInfoList)){
            cartInfoList.stream().map(cartInfoJson -> {
                CartInfo cartInfo = JSON.parseObject(cartInfoJson.toString(), CartInfo.class);
                cartInfo.setIsChecked(isChecked);
                return cartInfo;
            }).forEach(cartInfo -> redisTemplate.opsForHash().put(cartKey,
                    String.valueOf(cartInfo.getSkuId()), JSON.toJSONString(cartInfo)));
            }
        }

    @Override
    public void addToCart(Long skuId, Integer skuNum) {
        //获取当前登录用户id
        Long userId = AuthContextUtil.getUserInfo().getId();
        String cartKey = getCartKey(userId);

        //获取缓存对象
        Object cartInfoObj = redisTemplate.opsForHash().get(cartKey, String.valueOf(skuId));
        CartInfo cartInfo = null;
        //  如果购物车中有该商品，则商品数量 相加！
        if (cartInfoObj != null){
            cartInfo = JSON.parseObject(cartInfoObj.toString() , CartInfo.class) ;
            cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
            cartInfo.setIsChecked(1);
            cartInfo.setUpdateTime(new Date());
        }else{
            // 当购物车中没用该商品的时候，则直接添加到购物车！
            cartInfo = new CartInfo();
            // 购物车数据是从商品详情得到 {skuInfo}
            ProductSku productSku = productFeignClient.getBySkuId(skuId);
            cartInfo.setCartPrice(productSku.getSalePrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setSkuId(skuId);
            cartInfo.setUserId(userId);
            cartInfo.setImgUrl(productSku.getThumbImg());
            cartInfo.setSkuName(productSku.getSkuName());
            cartInfo.setIsChecked(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());
        }
        // 将商品数据存储到购物车中
        redisTemplate.opsForHash().put(cartKey , String.valueOf(skuId) , JSON.toJSONString(cartInfo));
    }

    @Override
    public List<CartInfo> cartList() {
        Long userId = AuthContextUtil.getUserInfo().getId();
        String cartKey = getCartKey(userId);
        List<Object> cartInfoList = redisTemplate.opsForHash().values(cartKey);
        if (!CollectionUtils.isEmpty(cartInfoList)){
            List<CartInfo> cartInfos = cartInfoList.stream().map(cartInfoJson -> JSON.parseObject(cartInfoJson.toString(), CartInfo.class))
                    .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).collect(Collectors.toList());
            return cartInfos;
        }
        return new ArrayList<>();
    }

    @Override
    public void deleteCart(Long skuId) {
        // 获取当前登录的用户数据
        Long userId = AuthContextUtil.getUserInfo().getId();
        String cartKey = getCartKey(userId);

        //获取缓存对象
        redisTemplate.opsForHash().delete(cartKey  ,String.valueOf(skuId)) ;
    }

    @Override
    public void checkCart(Long skuId, Integer isChecked) {
        Long id = AuthContextUtil.getUserInfo().getId();
        String cartKey = getCartKey(id);
        Boolean hasKey = redisTemplate.opsForHash().hasKey(cartKey, String.valueOf(id));
        if (hasKey){
            String cartInfoJSON = redisTemplate.opsForHash().get(cartKey, String.valueOf(id)).toString();
            CartInfo cartInfo = JSON.parseObject(cartInfoJSON, CartInfo.class);
            cartInfo.setIsChecked(isChecked);
            redisTemplate.opsForHash().put(cartKey, String.valueOf(skuId), JSON.toJSONString(cartInfo));
        }
    }

    @Override
    public void clearCart() {
        String cartKey = getCartKey(AuthContextUtil.getUserInfo().getId());
        redisTemplate.delete(cartKey);
    }

    @Override
    public List<CartInfo> getAllCkecked() {
        Long userId = AuthContextUtil.getUserInfo().getId();
        String cartKey = getCartKey(userId);
        List<Object> cartInfoList = redisTemplate.opsForHash().values(cartKey);
        List<CartInfo> cartInfos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cartInfoList)){
            cartInfos = cartInfoList.stream()
                    .map(cartInfoJson -> JSON.parseObject(cartInfoJson.toString(), CartInfo.class))
                    .filter(cartInfo -> cartInfo.getIsChecked() == 1).collect(Collectors.toList());
        }
        return cartInfos;
    }

    @Override
    public void deleteChecked() {
        UserInfo userInfo = AuthContextUtil.getUserInfo();
        String cartKey = getCartKey(userInfo.getId());
        // 删除选中的购物项数据
        List<Object> objectList = redisTemplate.opsForHash().values(cartKey);       // 删除选中的购物项数据
        if (!CollectionUtils.isEmpty(objectList)) {
            objectList.stream().map(cartInfoJSON -> JSON.parseObject(cartInfoJSON.toString(), CartInfo.class))
                    .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                    .forEach(cartInfo -> redisTemplate.opsForHash().delete(cartKey, String.valueOf(cartInfo.getSkuId())));
        }
    }

    private String getCartKey(Long userId) {
        //定义key user:cart:userId
        return "user:cart:" + userId;
    }
}
