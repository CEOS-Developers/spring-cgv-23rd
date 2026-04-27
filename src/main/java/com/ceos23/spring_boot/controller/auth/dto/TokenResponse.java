package com.ceos23.spring_boot.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
