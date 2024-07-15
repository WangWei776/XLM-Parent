package com.atguigu.spzx.pay;

import com.atguigu.spzx.common.annotation.EnableUserWebMvcConfiguration;
import com.atguigu.spzx.feign.order.OrderFeignClient;
import com.atguigu.spzx.feign.product.ProductFeignClient;
import com.atguigu.spzx.pay.properties.AlipayProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableUserWebMvcConfiguration
@EnableFeignClients(clients = {OrderFeignClient.class, ProductFeignClient.class})
@EnableConfigurationProperties(value = { AlipayProperties.class })
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class , args) ;
    }
}
