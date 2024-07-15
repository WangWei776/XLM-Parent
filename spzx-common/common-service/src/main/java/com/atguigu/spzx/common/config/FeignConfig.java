package com.atguigu.spzx.common.config;

import com.atguigu.spzx.common.feign.UserTokenFeignInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new UserTokenFeignInterceptor();
    }
}
