package com.ceos.spring_boot.domain.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    CONFIRMED("예매 완료"),
    CANCELED("예매 취소");

    private final String description;// 예매 취소
}
