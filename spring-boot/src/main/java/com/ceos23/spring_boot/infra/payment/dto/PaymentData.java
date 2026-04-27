package com.ceos23.spring_boot.infra.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PaymentData {

    private String paymentId;
    private String paymentStatus;
    private String orderName;
    private String pgProvider;
    private String currency;
    private String customData;
    private LocalDateTime paidAt;
}