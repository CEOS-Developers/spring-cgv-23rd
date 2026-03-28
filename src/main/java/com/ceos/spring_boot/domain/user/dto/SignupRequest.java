package com.ceos.spring_boot.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest (

        @NotBlank(message = "필수 입력 값입니다.")
        @Schema(description = "사용자 이름", example = "김동욱")
        String name,

        @NotBlank(message = "필수 입력 값입니다.")
        @Schema(description = "사용자 별명", example = "감자")
        String nickname,

        @NotBlank(message = "필수 입력 값입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @Schema(description = "비밀번호(8자 이상)", example = "password1234")
        String password,

        @NotBlank(message = "필수 입력 값입니다.")
        @Email(message = "이메일 형식에 맞지 않습니다.")
        @Schema(description = "사용자 이메일", example = "test@ceos.com")
        String email,

        @NotBlank(message = " 필수 입력 값입니다.")
        @Schema(description = "사용자 핸드폰 번호", example = "010-1234-5678")
        String phoneNumber
        
) {}
