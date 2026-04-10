package com.ceos23.cgv.domain.auth.dto;

public record UserResponse(
        Long userId,
        String email,
        String nickname
) {
}