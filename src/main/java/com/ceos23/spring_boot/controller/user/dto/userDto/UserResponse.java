package com.ceos23.spring_boot.controller.user.dto.userDto;

import com.ceos23.spring_boot.domain.user.dto.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 프로필 조회 응답 DTO")
public record UserResponse(
        @Schema(description = "회원 고유 ID", example = "1")
        Long memberId,

        @Schema(description = "이메일", example = "user@naver.com")
        String email,

        @Schema(description = "이름", example = "홍길동")
        String name,

        @Schema(description = "권한", example = "USER")
        String role
) {
    public static UserResponse from(UserInfo info) {
        return new UserResponse(
                info.memberId(),
                info.email(),
                info.name(),
                info.role()
        );
    }
}
