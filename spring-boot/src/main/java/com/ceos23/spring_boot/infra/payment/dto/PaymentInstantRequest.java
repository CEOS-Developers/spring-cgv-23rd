package com.ceos23.spring_boot.infra.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentInstantRequest {

    private String storeId;
    private String orderName;
    private Integer totalPayAmount;
    private String currency;
    private String customData;
}