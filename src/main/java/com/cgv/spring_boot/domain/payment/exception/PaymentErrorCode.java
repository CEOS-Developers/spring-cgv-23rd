package com.cgv.spring_boot.domain.payment.exception;

import com.cgv.spring_boot.global.common.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements BaseErrorCode {
    PAYMENT_NOT_FOUND(404, "해당 결제 내역을 찾을 수 없습니다."),
    PAYMENT_ALREADY_EXISTS(409, "해당 예매 건의 결제가 이미 생성되어 있습니다."),
    PAYMENT_NOT_CANCELLABLE(409, "현재 상태에서는 결제를 취소할 수 없습니다."),
    PAYMENT_FAILED(500, "결제 처리에 실패했습니다."),
    PAYMENT_SERVER_UNAVAILABLE(503, "외부 결제 서버와 통신할 수 없습니다."),
    INVALID_PAYMENT_RESPONSE(502, "외부 결제 서버 응답이 올바르지 않습니다."),
    INVALID_PAYMENT_STATUS(400, "유효하지 않은 결제 상태입니다.");

    private final int status;
    private final String message;
}
