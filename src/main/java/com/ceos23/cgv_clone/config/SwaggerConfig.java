package com.ceos23.cgv_clone.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🎬 CGV 클론 프로젝트 API")
                        .description("영화 예매, 매점 구매, 찜 기능 등을 제공하는 백엔드 API 명세서입니다.")
                        .version("v1.0.0"));
    }
}
