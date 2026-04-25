package com.ceos23.spring_boot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotNull(message = "userId는 필수입니다.")
    @Positive(message = "userId는 1 이상이어야 합니다.")
    private Long userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}