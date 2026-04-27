package com.ceos.spring_boot.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PAID("결제 완료"),
    READY("결제 대기"),
    CANCELLED("결제 취소"),
    FAILED("결제 실패");

    private final String description;
}
