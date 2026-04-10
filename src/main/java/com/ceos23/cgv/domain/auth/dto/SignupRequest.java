package com.ceos23.cgv.domain.auth.dto;

public record SignupRequest(
        String name,
        String email,
        String nickname,
        String password
) {
}