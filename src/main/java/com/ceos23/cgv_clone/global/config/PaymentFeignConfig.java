package com.ceos23.cgv_clone.global.config;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Slf4j
public class PaymentFeignConfig {

    @Bean
    public RequestInterceptor paymentAuthInterceptor(@Value("${payment.secret-key}") String secretKey)
    {
        return template -> template.header("Authorization", "Bearer " + secretKey);
    }

    @Bean
    public ErrorDecoder paymentErrorDecoder() {
        return (methodKey, response) -> {
            String body = readBody(response);
            log.warn("결제 API 실패: status={}, body={}", response.status(), body);
            if (response.status() >= 500) {
                return new CustomException(ErrorCode.PAYMENT_SERVER_ERROR);
            }
            return new CustomException(ErrorCode.PAYMENT_FAILED);
        };
    }

    private String readBody(feign.Response response) {
        if (response.body() == null) return "";
        try (BufferedReader r = new BufferedReader(new
                InputStreamReader(response.body().asInputStream()))) {
            return r.lines().collect(Collectors.joining());
        } catch (Exception e) {
            return "";
        }
    }
}
