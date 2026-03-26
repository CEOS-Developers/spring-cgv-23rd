package com.ceos23.spring_boot.global.security.dto;

public record AccessTokenInfo(
        String userId,
        String role
) {
}
