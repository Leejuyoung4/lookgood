package com.oo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // origins 대신 originPatterns 사용
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(false);     // credentials를 false로 설정
    }
}