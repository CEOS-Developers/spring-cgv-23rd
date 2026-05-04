package com.ceos.spring_cgv_23rd.domain.product.exception;

import com.ceos.spring_cgv_23rd.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductErrorCode implements BaseErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_4041", "존재하지 않는 상품입니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_4042", "존재하지 않는 주문입니다."),
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_4043", "해당 영화관에 존재하지 않는 상품입니다."),

    ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "PRODUCT_4001", "이미 취소된 주문입니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "PRODUCT_4002", "재고가 부족합니다."),
    EMPTY_ORDER_ITEMS(HttpStatus.BAD_REQUEST, "PRODUCT_4003", "주문 항목이 없습니다."),
    DUPLICATE_ORDER_ITEM(HttpStatus.BAD_REQUEST, "PRODUCT_4004", "중복된 상품이 주문에 포함되어 있습니다."),

    ORDER_FORBIDDEN(HttpStatus.FORBIDDEN, "PRODUCT_4031", "해당 주문에 대한 권한이 없습니다."),

    CONFIRM_FAILED_ROLLED_BACK(HttpStatus.INTERNAL_SERVER_ERROR, "PRODUCT_5001", "주문 확정 중 오류가 발생하여 결제가 취소되었습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
