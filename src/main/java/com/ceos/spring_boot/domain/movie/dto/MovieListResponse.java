package com.ceos.spring_boot.domain.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MovieListResponse(
        List<MovieResponse> movies,

        @Schema(description = "총 영화 수", example = "777")
        int count
) {
    public static MovieListResponse from(List<MovieResponse> movies) {
        return new MovieListResponse(movies, movies.size());
    }
}