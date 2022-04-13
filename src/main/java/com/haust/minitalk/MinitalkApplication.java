package com.haust.minitalk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

// 扫描 mapper
@MapperScan(value = "com.haust.minitalk.mapper")
@SpringBootApplication
public class MinitalkApplication extends SpringBootServletInitializer {

    /*
     * 注入 springUtil
     * @return: com.haust.minitalk.SpringUtil
     * @create: 2020/9/25 15:26
     * @author: csp1999
     */
    @Bean
    public SpringUtil getSpringUtil(){
        return new SpringUtil();
    }

    public static void main(String[] args) {
        SpringApplication.run(MinitalkApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MinitalkApplication.class);
    }
}
