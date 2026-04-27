package com.ceos.spring_boot.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentCategory {
    MOVIE("영화 결제"),
    STORE("매점 결제");

    private final String description;
}
