package com.ceos.spring_cgv_23rd.domain.payment.exception;

import com.ceos.spring_cgv_23rd.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements BaseErrorCode {


    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_4041", "결제 정보를 찾을 수 없습니다."),

    PAYMENT_IN_PROGRESS(HttpStatus.CONFLICT, "PAYMENT_4091", "이미 진행 중인 결제입니다."),
    PAYMENT_ALREADY_PAID(HttpStatus.CONFLICT, "PAYMENT_4092", "이미 결제 완료된 건입니다."),
    PAYMENT_ALREADY_FAILED(HttpStatus.CONFLICT, "PAYMENT_4093", "이미 실패한 결제입니다. 다시 예매해주세요."),
    PAYMENT_ALREADY_CANCELLED(HttpStatus.CONFLICT, "PAYMENT_4094", "이미 취소된 결제입니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "PAYMENT_4095", "잘못된 결제 상태입니다."),
    CANNOT_CANCEL_NOT_PAID(HttpStatus.CONFLICT, "PAYMENT_4096", "완료되지 않은 결제는 취소할 수 없습니다."),

    PG_CALL_FAILED(HttpStatus.BAD_GATEWAY, "PAYMENT_5021", "외부 결제 시스템 호출에 실패했습니다."),
    PG_PAYMENT_FAILED(HttpStatus.BAD_GATEWAY, "PAYMENT_5022", "결제가 실패했습니다."),
    PG_CANCEL_FAILED(HttpStatus.BAD_GATEWAY, "PAYMENT_5023", "결제 취소에 실패했습니다."),

    CONFIRM_FAILED_ROLLED_BACK(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_5001", "예매 확정 중 오류가 발생하여 결제가 취소되었습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}