package com.ceos23.cgv_clone.payment.dto.response;

import com.ceos23.cgv_clone.payment.entity.Currency;
import com.ceos23.cgv_clone.payment.entity.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {
    private String paymentId;
    private PaymentStatus paymentStatus;
    private String orderName;
    private String pgProvider;
    private Currency currency;
    private String customData;
    private LocalDateTime paidAt;
}
