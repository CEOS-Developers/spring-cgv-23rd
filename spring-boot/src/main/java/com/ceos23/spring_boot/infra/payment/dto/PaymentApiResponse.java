package com.ceos23.spring_boot.infra.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentApiResponse<T> {

    private Integer code;
    private String message;
    private T data;
}