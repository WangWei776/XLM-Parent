package com.atguigu.spzx.order;

import com.atguigu.spzx.common.annotation.EnableUserTokenFeignInterceptor;
import com.atguigu.spzx.common.annotation.EnableUserWebMvcConfiguration;
import com.atguigu.spzx.feign.cart.CartFeignClient;
import com.atguigu.spzx.feign.product.ProductFeignClient;
import com.atguigu.spzx.feign.user.UserFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//  com.atguigu.spzx.order;
@SpringBootApplication
@EnableFeignClients(clients = {CartFeignClient.class, UserFeignClient.class, ProductFeignClient.class})
@EnableUserTokenFeignInterceptor
@EnableUserWebMvcConfiguration
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class , args) ;
    }

}