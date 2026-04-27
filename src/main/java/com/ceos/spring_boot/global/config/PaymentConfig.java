package com.ceos.spring_boot.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@Getter
public class PaymentConfig {

    @Value("${payment.base-url}")
    private String baseUrl;

    @Value("${payment.store-id}")
    private String storeId;

    @Value("${payment.secret-key}")
    private String secretKey;

    @Bean
    public RestClient paymentRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + secretKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}