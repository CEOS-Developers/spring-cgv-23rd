package com.ceos23.spring_boot.infra.payment.dto;

import java.time.LocalDateTime;

public record PaymentAuthData(
        String githubId,
        String apiSecretKey,
        LocalDateTime createdAt
) {
}