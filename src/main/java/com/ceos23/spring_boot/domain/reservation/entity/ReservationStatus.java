package com.ceos23.spring_boot.domain.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    PAID("결제 완료"),
    PENDING("결제 대기 중"),
    CANCELED("결제 취소");

    private final String description;
}
