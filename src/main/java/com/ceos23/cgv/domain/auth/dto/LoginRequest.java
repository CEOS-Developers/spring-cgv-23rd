package com.ceos23.cgv.domain.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}