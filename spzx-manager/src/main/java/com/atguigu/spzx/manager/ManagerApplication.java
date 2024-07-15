package com.atguigu.spzx.manager;
import com.atguigu.spzx.common.log.annotation.EnableLogAspect;
import com.atguigu.spzx.manager.service.properties.MinioProperties;
import com.atguigu.spzx.manager.service.properties.UserAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.atguigu.spzx"})
@EnableConfigurationProperties(value = {UserAuthProperties.class, MinioProperties.class})
@EnableScheduling
@EnableLogAspect
public class ManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManagerApplication.class, args);
    }
}
