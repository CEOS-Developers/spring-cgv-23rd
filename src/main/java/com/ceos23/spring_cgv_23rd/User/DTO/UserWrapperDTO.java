package com.ceos23.spring_cgv_23rd.User.DTO;

import com.ceos23.spring_cgv_23rd.User.Domain.User;

public record UserWrapperDTO(
        long userId,
        String username,
        boolean men,
        int age
) {
    public static UserWrapperDTO create(User user){
        return new UserWrapperDTO(user.getId(), user.getUsername(), user.isMen(), user.getAge());
    }
}
