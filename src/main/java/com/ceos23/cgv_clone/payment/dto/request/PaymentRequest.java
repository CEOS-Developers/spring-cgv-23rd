package com.ceos23.cgv_clone.payment.dto.request;

import com.ceos23.cgv_clone.payment.entity.Currency;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequest {
    private String storeId;
    private String orderName;
    private int totalPayAmount;
    private Currency currency;
    private String customData;
}
