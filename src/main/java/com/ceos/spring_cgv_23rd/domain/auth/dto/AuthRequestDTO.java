package com.ceos.spring_cgv_23rd.domain.auth.dto;

import com.ceos.spring_cgv_23rd.domain.user.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

public class AuthRequestDTO {

    @Builder
    public record SignupRequestDTO(
            @NotBlank @Size(max = 20)
            String username,

            @NotBlank @Size(min = 8, max = 20)
            String password,

            @NotBlank @Size(max = 20)
            String name,

            @NotBlank @Email @Size(max = 50)
            String email,

            @NotBlank @Size(max = 20)
            String phone,

            LocalDate birth,

            @NotBlank @Size(max = 20)
            String nickname,

            @NotNull
            Gender gender
    ) {
    }

    @Builder
    public record LoginRequestDTO(
            @NotBlank
            String username,

            @NotBlank
            String password
    ) {
    }
}
