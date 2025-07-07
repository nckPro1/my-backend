package com.example.bida.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173") // 👈 đúng port frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 👈 thêm OPTIONS
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
