package com.ceos.spring_cgv_23rd.domain.guest.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class Guest {

    private Long id;
    private String name;
    private String phone;
    private LocalDate birth;
    private String password;

    public static Guest createGuest(String name, String phone, LocalDate birth, String encodedPassword) {
        return Guest.builder()
                .name(name)
                .phone(phone)
                .birth(birth)
                .password(encodedPassword)
                .build();
    }
}
