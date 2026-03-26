package com.ceos23.spring_boot.domain.user.dto;

import com.ceos23.spring_boot.domain.movie.entity.Movie;
import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.domain.user.entity.FavoriteMovie;
import com.ceos23.spring_boot.domain.user.entity.FavoriteTheater;

public record FavoriteMovieInfo(
        Movie movie
) {
    public static FavoriteMovieInfo from(FavoriteMovie favoriteMovie) {
        return new FavoriteMovieInfo(
                favoriteMovie.getMovie()
        );
    }
}
