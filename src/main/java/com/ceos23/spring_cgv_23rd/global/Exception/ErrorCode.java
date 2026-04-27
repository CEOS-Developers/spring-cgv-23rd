package com.ceos23.spring_cgv_23rd.global.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
     /*
    400: badRequest
    401: Unauthorized
    403: forbidden
    404: NotFound
    500: InternalServerError
     */

    //Global
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "서버에 장애가 발생했습니다."),
    DATA_ACCESS_EXCEPTION(HttpStatus.CONFLICT, "G002", "이미 등록된 유일키입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "G003", "권한이 없습니다."),

    //Exception for DataBase
    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, "D001", "결제정보를 찾을 수 없습니다"),
    NOT_FOUND_RESERVATION(HttpStatus.NOT_FOUND, "D002", "예약정보를 찾을 수 없습니다."),
    NOT_FOUND_CART(HttpStatus.NOT_FOUND, "D003", "장바구니를 찾을 수 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "D004", "유저를 찾을 수 없습니다."),
    NOT_FOUND_THEATER(HttpStatus.NOT_FOUND, "D005", "영화관을 찾을 수 없습니다."),
    NOT_FOUND_MENU(HttpStatus.NOT_FOUND, "D006", "해당 메뉴를 찾을 수 없습니다."),
    NOT_FOUND_MOVIE(HttpStatus.NOT_FOUND, "D007", "해당 영화를 찾을 수 없습니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "D008", "토큰을 찾을 수 없습니다."),

    //Exception for Authentication
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "A001", "잘못된 형식의 토큰입니다."),
    COUNTERFEIT_SIGNATURE(HttpStatus.BAD_REQUEST, "A002", "토큰의 서명이 위조되었습니다."),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "A003", "만료된 토큰입니다."),
    REUSE_DETECTED(HttpStatus.UNAUTHORIZED, "A004", "이미 사용된 리프레시 토큰이 사용되었습니다."),
    REVOKED_TOKEN(HttpStatus.BAD_REQUEST, "A005", "이미 폐기된 토큰입니다."),
    CANNOT_FIND_TOKEN(HttpStatus.BAD_REQUEST, "A006", "토큰을 찾을 수 없습니다."),

    //Exception for paying
    STORE_ID_MISMATCH(HttpStatus.FORBIDDEN, "P001", "결제 연동에 실패했습니다. (사유: 가맹점 storeId 불일치)"),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "P002", "결제 연동에 실패했습니다. (사유: 존재하지 않는 가맹점)"),
    DUPLICATE_PAYMENT_ID(HttpStatus.CONFLICT, "P003", "이미 존재하는 paymentId입니다."),
    PAYMENT_FAILED_BY_OUTER_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "P004", "결제 처리 중 외부 연동에 실패했습니다."),
    PAYMENT_FAILED_BY_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "P005", "결제에 실패했습니다."),

    CART_IS_UNAVAILABLE(HttpStatus.BAD_REQUEST, "P006", "이미 결제가 완료되거나 진행 중인 장바구니입니다."),
    RESERVATION_IS_UNAVAILABLE(HttpStatus.BAD_REQUEST, "P007", "이미 결제가 완료되거나 진행 중인 예약입니다."),

    //Exception while reservation
    ALREADY_OCCUPIED(HttpStatus.BAD_REQUEST, "R001", "이미 선택된 좌석입니다."),
    DIFFERENT_USER(HttpStatus.BAD_REQUEST, "R002", "사용자와 예약자가 다릅니다."),
    DUPLICATION_SEAT(HttpStatus.BAD_REQUEST, "R003", "좌석을 중복선택할 수 없습니다."),

    //Exception while ordering Food
    INVENTORY_SHORTAGE(HttpStatus.BAD_REQUEST, "O001", "재고가 부족합니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "O002", "재고는 음수로 할 수 없습니다."),

    //Exception for canceling
    PAYMENT_NOT_CANCELLABLE(HttpStatus.CONFLICT, "P006", "PAID 상태에서만 취소할 수 있습니다."),
    CANCEL_FAILED_BY_OUTER_SERVER(HttpStatus.CONFLICT, "P006", "취소 처리 중 외부 연동에 실패했습니다."),

    //Exception for signup
    USER_NAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "S001", "이미 사용 중인 사용자명입니다."),
    LOGIN_ID_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "S002", "중복된 아이디입니다."),
    PASSWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "S003", "비밀번호는 8자 이상이어야합니다."),
    PASSWORD_TOO_SIMPLE(HttpStatus.BAD_REQUEST, "S004", "비밀번호는 반드시 * 또는 !을 포함해야합니다."),

    //Exception for login
    ID_NOT_FOUND(HttpStatus.NOT_FOUND, "L001", "아이디가 올바르지 않습니다."),
    UNMATCHED_PASSWORD(HttpStatus.BAD_REQUEST, "L002", "비밀번호가 올바르지 않습니다.");


    private final HttpStatus status;

    private final String errorCode;

    private final String errorMessage;

    ErrorCode(HttpStatus status, String code, String message){
        this.status = status;
        this.errorCode = code;
        this.errorMessage = message;
    }
}