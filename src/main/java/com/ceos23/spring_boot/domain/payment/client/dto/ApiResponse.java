package com.ceos23.spring_boot.domain.payment.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiResponse<T> (
        @JsonProperty("status")
        Integer code,
        String message,
        @JsonProperty("payload")
        T data
){
}
