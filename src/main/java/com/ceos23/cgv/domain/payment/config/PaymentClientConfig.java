package com.ceos23.cgv.domain.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentClientConfig {

    @Bean
    public RestClient paymentRestClient(PaymentProperties paymentProperties) {
        return RestClient.builder()
                .baseUrl(paymentProperties.getBaseUrl())
                .build();
    }
}
