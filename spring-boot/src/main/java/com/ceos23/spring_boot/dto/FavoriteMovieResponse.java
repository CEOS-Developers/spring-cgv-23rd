package com.ceos23.spring_boot.dto;

import com.ceos23.spring_boot.domain.FavoriteMovie;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteMovieResponse {

    private Long favoriteMovieId;
    private Long movieId;
    private String title;

    public static FavoriteMovieResponse from(FavoriteMovie favoriteMovie) {
        return FavoriteMovieResponse.builder()
                .favoriteMovieId(favoriteMovie.getId())
                .movieId(favoriteMovie.getMovie().getId())
                .title(favoriteMovie.getMovie().getTitle())
                .build();
    }
}