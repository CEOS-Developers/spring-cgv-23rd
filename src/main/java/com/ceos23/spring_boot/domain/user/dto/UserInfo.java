package com.ceos23.spring_boot.domain.user.dto;

import com.ceos23.spring_boot.domain.user.entity.User;

public record UserInfo(
        Long memberId,
        String email,
        String name,
        String role
) {
    public static UserInfo from(User user) {
        return new UserInfo(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }
}
