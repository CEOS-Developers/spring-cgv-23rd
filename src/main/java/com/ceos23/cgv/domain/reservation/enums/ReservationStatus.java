package com.ceos23.cgv.domain.reservation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    PENDING("결제 대기"),
    BOOKED("예매 완료"),
    CANCELED("예매 취소"),
    COMPLETED("결제 완료");

    private final String description;
}
