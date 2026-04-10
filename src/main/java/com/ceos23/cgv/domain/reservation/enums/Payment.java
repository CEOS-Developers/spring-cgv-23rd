package com.ceos23.cgv.domain.reservation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Payment {
    APP_CARD("앱카드"),
    MOBILE("휴대폰 결제"),
    BANK_ACCOUNT("계좌이체"),
    KAKAO_PAY("카카오페이"),
    NAVER_PAY("네이버페이"),
    TOSS("토스페이"),
    POINT("포인트 결제"),
    EZWEL_PAY("이지웰페이"),
    SMILE_PAY("스마일페이"),
    PAYCO("페이코"),
    SSG_PAY("SSG페이"),
    CJ_PAY("CJ ONE 페이");

    private final String description;
}