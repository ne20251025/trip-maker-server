package com.example.trip_maker_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // (★) 1. application.properties에서 방금 설정한 'public/uploads' 경로를 가져옴
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        // (★) 2. http://.../uploads/** (웹 경로)로 요청이 오면
        // (★) 3. file:///Users/ne/.../public/uploads/ (실제 파일 경로)에서 찾아 제공
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}