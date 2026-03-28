package com.ceos.spring_boot.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SignupResponse(
        @Schema(description = "사용자 고유 ID", example = "1")
        Long userId,

        @Schema(description = "사용자 이름", example = "김동욱")
        String name,

        @Schema(description = "사용자 이메일", example = "test@ceos.com")
        String email,

        @Schema(description = "사용자 닉네임", example = "감자")
        String nickname,

        @Schema(description = "사용자 핸드폰", example = "010-1234-5678")
        String phoneNumber
) {
    public static SignupResponse of(Long userId, String name, String email, String nickname, String phoneNumber
    ) {
        return SignupResponse.builder()
                .userId(userId)
                .name(name)
                .email(email)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .build();
    }
}
