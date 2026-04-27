package com.ceos.spring_boot.domain.payment.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentApiWrapper<T>(
        @JsonProperty("status")
        @Schema(description = "응답 코드", example = "200")
        int code,

        @Schema(description = "응답 메시지", example = "결제 처리 완료")
        String message,

        @JsonProperty("payload")
        @Schema(description = "실제 응답 데이터")
        T data
) {}
