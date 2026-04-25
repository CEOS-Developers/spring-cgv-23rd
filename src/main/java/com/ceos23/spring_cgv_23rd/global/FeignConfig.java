package com.ceos23.spring_cgv_23rd.global;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Value("${concurrency.secret-key}")
    private String apiSecretKey;

    @Bean
    public RequestInterceptor requestInterceptor(){
        return restTemplate -> restTemplate.header("Authorization", "Bearer " + apiSecretKey);
    }
}
