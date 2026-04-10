package com.ceos23.spring_boot.cgv.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank String refreshToken
) {
}