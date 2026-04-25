package com.ceos.spring_cgv_23rd.domain.payment.application.dto.command;

public record PayCommand(
        String paymentId,
        String orderName,
        int amount
) {
}
