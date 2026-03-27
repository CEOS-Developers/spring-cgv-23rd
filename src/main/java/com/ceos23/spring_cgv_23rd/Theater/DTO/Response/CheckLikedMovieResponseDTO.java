package com.ceos23.spring_cgv_23rd.Theater.DTO.Response;

import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieWrapperDTO;
import com.ceos23.spring_cgv_23rd.User.Domain.User;

import java.util.List;

public record CheckLikedMovieResponseDTO(
        long userId,
        String userName,

        List<MovieWrapperDTO> movies
) {
    public static CheckLikedMovieResponseDTO crate(User user, List<MovieWrapperDTO> wrappers){
        return new CheckLikedMovieResponseDTO(user.getId(), user.getUsername(), wrappers);
    }
}
