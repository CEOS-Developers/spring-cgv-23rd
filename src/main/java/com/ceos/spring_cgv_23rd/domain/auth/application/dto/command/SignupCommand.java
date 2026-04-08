package com.ceos.spring_cgv_23rd.domain.auth.application.dto.command;

import com.ceos.spring_cgv_23rd.domain.user.domain.Gender;

import java.time.LocalDate;

public record SignupCommand(
        String username,
        String password,
        String name,
        String email,
        String phone,
        LocalDate birth,
        String nickname,
        Gender gender
) {
}
