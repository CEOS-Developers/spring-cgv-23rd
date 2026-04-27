package com.ceos23.spring_cgv_23rd.Movie.DTO.Response;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.RequestType;

public record LikedMovieResponseDTO(
        RequestType type,
        long movieId, String movieName
) {
    public static LikedMovieResponseDTO create(RequestType type, Movie movie){
        return new LikedMovieResponseDTO(type, movie.getId(), movie.getMovieName());
    }
}
