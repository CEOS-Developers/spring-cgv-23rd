package com.ceos.spring_cgv_23rd.domain.reservation.exception;

import com.ceos.spring_cgv_23rd.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReservationErrorCode implements BaseErrorCode {

    GUEST_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "RESERVATION_4011", "비회원 인증에 실패했습니다."),

    ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "RESERVATION_4001", "이미 취소된 예매입니다."),
    NO_REMAINING_SEATS(HttpStatus.BAD_REQUEST, "RESERVATION_4002", "남은 좌석이 부족합니다."),
    INVALID_SEAT_COUNT(HttpStatus.BAD_REQUEST, "RESERVATION_4003", "좌석 수가 올바르지 않습니다."),
    SEAT_HOLD_EXPIRED(HttpStatus.BAD_REQUEST, "RESERVATION_4004", "좌석 점유가 만료되었습니다."),

    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_4041", "존재하지 않는 예매입니다."),
    SCREENING_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_4042", "존재하지 않는 상영 스케줄입니다."),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_4043", "존재하지 않는 좌석입니다."),

    RESERVATION_FORBIDDEN(HttpStatus.FORBIDDEN, "RESERVATION_4031", "해당 예매에 대한 권한이 없습니다."),

    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "RESERVATION_4091", "이미 예약된 좌석입니다."),

    CONFIRM_FAILED_ROLLED_BACK(HttpStatus.INTERNAL_SERVER_ERROR, "RESERVATION_5001", "예매 확정 중 오류가 발생하여 결제가 취소되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
