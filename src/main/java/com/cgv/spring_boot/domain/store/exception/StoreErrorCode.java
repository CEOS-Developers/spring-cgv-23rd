package com.cgv.spring_boot.domain.store.exception;

import com.cgv.spring_boot.global.common.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements BaseErrorCode {
    INVALID_STOCK_QUANTITY(400, "재고 수량은 1개 이상이어야 합니다."),
    ITEM_NOT_FOUND(404, "해당 상품을 찾을 수 없습니다."),
    STORE_INVENTORY_NOT_FOUND(404, "해당 매점 재고를 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(400, "재고가 부족합니다.");

    private final int status;
    private final String message;
}
