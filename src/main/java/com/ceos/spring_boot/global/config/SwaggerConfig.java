package com.ceos.spring_boot.global.config;

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
                        .title("CGV API 명세서")
                        .description("CEOS 23기 백엔드 과제 - 영화관 관리 API")
                        .version("v1.0.0"));
    }
}
