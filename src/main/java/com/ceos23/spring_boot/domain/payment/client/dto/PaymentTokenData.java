package com.ceos23.spring_boot.domain.payment.client.dto;

public record PaymentTokenData(
        String githubId,
        String apiSecretKey,
        String createdAt
) {}
