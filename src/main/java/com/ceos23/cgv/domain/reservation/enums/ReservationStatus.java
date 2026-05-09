package com.ceos23.cgv.domain.reservation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {

    PENDING("예약 대기"),
    BOOKED("예약 완료"),
    CANCELED("예약 취소"),
    COMPLETED("관람 완료");

    private final String description;
}