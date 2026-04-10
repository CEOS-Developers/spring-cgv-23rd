package com.ceos.spring_boot.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AuthResponse(
        @Schema(description = "사용자 고유 ID", example = "1")
        Long userId,

        @Schema(description = "사용자 이름", example = "김동욱")
        String name,

        @Schema(description = "사용자 이메일", example = "test@ceos.com")
        String email,

        @Schema(description = "사용자 닉네임", example = "감자")
        String nickname,

        @Schema(description = "Access Token", example = "eyJhbGciOiJIUzUxMiJ1...")
        String accessToken
) {
    public static AuthResponse of(Long userId, String name, String email, String nickname, String accessToken) {
        return AuthResponse.builder()
                .userId(userId)
                .name(name)
                .email(email)
                .nickname(nickname)
                .accessToken(accessToken)
                .build();
    }
}