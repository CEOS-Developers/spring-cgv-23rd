package com.ceos.spring_cgv_23rd.domain.payment.adapter.out.pg.dto;

public record PgErrorResponse(
        Integer status,
        String error,
        String message,
        String path,
        String timestamp
) {
}
