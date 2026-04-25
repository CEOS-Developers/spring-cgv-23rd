package com.ceos.spring_boot.domain.store.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("주문 대기"),
    COMPLETED("주문 완료"),
    PREPARING("상품 준비 중"),
    CANCELLED("주문 취소");

    private final String description;
}
