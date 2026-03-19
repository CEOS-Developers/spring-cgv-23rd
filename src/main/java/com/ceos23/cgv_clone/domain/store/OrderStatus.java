package com.ceos23.cgv_clone.domain.store;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING,
    PAID,
    FAILED,
    CANCELED;
}
