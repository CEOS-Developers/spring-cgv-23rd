package com.ceos23.spring_cgv_23rd.Movie.DTO.Request;

import jakarta.validation.constraints.NotNull;

public record BookmarkMovieRequestDTO(
        @NotNull
        Long movieId
) {
}
