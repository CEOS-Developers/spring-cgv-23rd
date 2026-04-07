package com.ceos.spring_cgv_23rd.domain.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class User {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private LocalDate birth;
    private String nickname;
    private UserRole role;
    private String profileImageUrl;
    private Gender gender;

}
