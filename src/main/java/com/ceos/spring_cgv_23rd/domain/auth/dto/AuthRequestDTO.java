package com.ceos.spring_cgv_23rd.domain.auth.dto;

import com.ceos.spring_cgv_23rd.domain.user.domain.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

public class AuthRequestDTO {

    @Builder
    public record SignupRequestDTO(
            @NotBlank(message = "아이디가 없습니다.")
            @Size(max = 20, message = "아이디는 20자 이하로 입력해주세요.")
            String username,

            @NotBlank(message = "비밀번호가 없습니다.")
            @Pattern(
                    regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
                    message = "비밀번호는 8~16자이며, 영문·숫자·특수문자를 모두 포함해야 합니다."
            )
            String password,

            @NotBlank(message = "이름이 없습니다.")
            @Size(max = 20, message = "이름은 20자 이하로 입력해주세요.")
            String name,

            @NotBlank(message = "이메일이 없습니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            @Size(max = 50, message = "이메일은 50자 이하로 입력해주세요.")
            String email,

            @NotBlank(message = "전화번호가 없습니다.")
            @Pattern(
                    regexp = "^010\\d{7,8}$",
                    message = "전화번호 형식이 올바르지 않습니다."
            )
            String phone,

            @NotNull(message = "생년월일이 없습니다.")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            LocalDate birth,

            @NotBlank(message = "닉네임이 없습니다.")
            @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
            String nickname,

            @NotNull(message = "성별이 없습니다.")
            Gender gender
    ) {
    }

    @Builder
    public record LoginRequestDTO(
            @NotBlank(message = "아이디가 없습니다.")
            String username,

            @NotBlank(message = "비밀번호가 없습니다.")
            String password
    ) {
    }
}
