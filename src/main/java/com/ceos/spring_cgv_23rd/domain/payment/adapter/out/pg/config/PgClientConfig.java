package com.ceos.spring_cgv_23rd.domain.payment.adapter.out.pg.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class PgClientConfig {

    @Value("${pg.base-url}")
    private String baseUrl;

    @Value("${pg.api-secret}")
    private String apiSecret;

    @Bean
    public RestClient pgRestClient() {

        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiSecret)
                .build();
    }
}
