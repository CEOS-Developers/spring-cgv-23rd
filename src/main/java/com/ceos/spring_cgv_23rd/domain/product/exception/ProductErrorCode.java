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
    ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "PRODUCT_4001", "이미 취소된 주문입니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "PRODUCT_4002", "재고가 부족합니다."),
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_4043", "해당 영화관에 존재하지 않는 상품입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
