package com.cgv.spring_boot.domain.reservation.exception;

import com.cgv.spring_boot.global.common.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements BaseErrorCode {
    RESERVATION_NOT_FOUND(404, "해당 예매 내역을 찾을 수 없습니다."),
    ALREADY_RESERVED_SEAT(400, "이미 예약된 좌석입니다."),
    INVALID_SEAT_POSITION(400, "유효하지 않은 좌석 위치입니다."),
    ALREADY_CANCELLED(400, "이미 취소된 예매입니다.");

    private final int status;
    private final String message;
}
