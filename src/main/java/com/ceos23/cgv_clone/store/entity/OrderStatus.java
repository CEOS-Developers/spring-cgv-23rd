package com.ceos23.cgv_clone.store.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING,
    PAID,
    FAILED,
    CANCELED
}
