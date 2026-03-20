package com.ceos23.spring_boot.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("영화관 API 명세서")
                .description("CGV 클론코딩 프로젝트 API 문서입니다.");

        return new OpenAPI()
                .info(info);
    }
}
