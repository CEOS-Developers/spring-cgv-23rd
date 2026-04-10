package com.ceos23.spring_boot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpResponse {

    private Long userId;
    private String name;
}