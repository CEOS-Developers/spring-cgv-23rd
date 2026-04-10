package com.ceos23.spring_boot.dto;

import com.ceos23.spring_boot.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyInfoResponse {

    private Long userId;
    private String name;

    public static MyInfoResponse from(User user) {
        return new MyInfoResponse(user.getId(), user.getName());
    }
}