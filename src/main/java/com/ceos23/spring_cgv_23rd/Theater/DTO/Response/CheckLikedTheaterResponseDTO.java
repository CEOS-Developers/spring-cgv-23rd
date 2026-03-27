package com.ceos23.spring_cgv_23rd.Theater.DTO.Response;

import com.ceos23.spring_cgv_23rd.User.Domain.User;

import java.util.List;

public record CheckLikedTheaterResponseDTO(
        long userId,
        String userName,

        List<TheaterWrapperDTO> res
) {
    public static CheckLikedTheaterResponseDTO create(User user, List<TheaterWrapperDTO> wrappers) {
        return new CheckLikedTheaterResponseDTO(
            user.getId(), user.getUsername(), wrappers
        );
    }
}
