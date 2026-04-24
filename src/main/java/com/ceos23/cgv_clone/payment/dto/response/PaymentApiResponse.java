package com.ceos23.cgv_clone.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentApiResponse {
    private int code;
    private String message;
    private PaymentResponse payload;
}
