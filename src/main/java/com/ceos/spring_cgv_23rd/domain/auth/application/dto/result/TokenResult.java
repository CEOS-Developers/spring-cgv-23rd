package com.ceos.spring_cgv_23rd.domain.auth.application.dto.result;

public record TokenResult(
        String accessToken,
        String refreshToken
) {
}
