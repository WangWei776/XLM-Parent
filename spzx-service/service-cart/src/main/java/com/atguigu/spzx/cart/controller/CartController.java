package com.atguigu.spzx.cart.controller;


import com.atguigu.spzx.cart.service.CartService;
import com.atguigu.spzx.model.entity.h5.CartInfo;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order/cart/auth")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/deleteChecked")
    public Result deleteChecked(){
        cartService.deleteChecked();
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @GetMapping(value = "/getAllCkecked")
    public List<CartInfo> getAllCkecked() {
        List<CartInfo> cartInfoList = cartService.getAllCkecked() ;
        return cartInfoList;
    }

    @GetMapping("/clearCart")
    public Result clearCart(){
        cartService.clearCart();
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/allCheckCart/{isChecked}")
    public Result allCheckCart(@PathVariable Integer isChecked){
        cartService.allCheckCart(isChecked);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable Long skuId,
                            @PathVariable Integer isChecked){
        cartService.checkCart(skuId, isChecked);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @DeleteMapping ("/deleteCart/{skuId}")
    public Result deleteCart(@PathVariable Long skuId){
        cartService.deleteCart(skuId);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/cartList")
    public Result cartList(){
        List<CartInfo> cartInfos = cartService.cartList();
        return Result.build(cartInfos, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/addToCart/{skuId}/{skuNum}")
    public Result addToCart(@Parameter(name = "skuId", description = "商品skuId", required = true) @PathVariable("skuId") Long skuId,
                            @Parameter(name = "skuNum", description = "数量", required = true) @PathVariable("skuNum") Integer skuNum) {
        cartService.addToCart(skuId, skuNum);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

}
