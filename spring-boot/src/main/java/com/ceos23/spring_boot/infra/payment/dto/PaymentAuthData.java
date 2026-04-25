package com.ceos23.spring_boot.infra.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PaymentAuthData {

    private String githubId;
    private String apiSecretKey;
    private LocalDateTime createdAt;
}