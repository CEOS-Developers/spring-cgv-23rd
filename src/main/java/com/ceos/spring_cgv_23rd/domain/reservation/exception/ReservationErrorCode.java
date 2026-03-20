package com.ceos.spring_cgv_23rd.domain.reservation.exception;

import com.ceos.spring_cgv_23rd.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReservationErrorCode implements BaseErrorCode {

    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_4041", "존재하지 않는 예매입니다."),

    ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "RESERVATION_4001", "이미 취소된 예매입니다."),

    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "RESERVATION_4091", "이미 예약된 좌석입니다."),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_4042", "존재하지 않는 좌석입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
