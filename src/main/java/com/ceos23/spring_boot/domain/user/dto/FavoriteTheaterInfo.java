package com.ceos23.spring_boot.domain.user.dto;

import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.domain.user.entity.FavoriteTheater;

public record FavoriteTheaterInfo(
    Theater theater
) {
    public static FavoriteTheaterInfo from(FavoriteTheater favoriteTheater) {
        return new FavoriteTheaterInfo(
            favoriteTheater.getTheater()
        );
    }
}
