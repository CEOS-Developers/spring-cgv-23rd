package com.ceos23.spring_cgv_23rd.User.DTO;

public record SignupRequestDTO(
        String username,
        String loginId,
        String password,
        boolean men,
        int age
) {

}
