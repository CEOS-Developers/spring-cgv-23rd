package com.ceos23.spring_boot.cgv.dto.auth;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken
) {
}