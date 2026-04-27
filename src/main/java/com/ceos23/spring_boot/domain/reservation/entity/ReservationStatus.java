package com.ceos23.spring_boot.domain.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    RESERVED("예매 완료"),
    CANCELED("예매 취소");

    private final String description;
}
