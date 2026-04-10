package com.ceos23.spring_boot.dto;

import com.ceos23.spring_boot.domain.FavoriteTheater;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteTheaterResponse {

    private Long favoriteTheaterId;
    private Long theaterId;
    private String theaterName;
    private String location;

    public static FavoriteTheaterResponse from(FavoriteTheater favoriteTheater) {
        return FavoriteTheaterResponse.builder()
                .favoriteTheaterId(favoriteTheater.getId())
                .theaterId(favoriteTheater.getTheater().getId())
                .theaterName(favoriteTheater.getTheater().getName())
                .location(favoriteTheater.getTheater().getLocation())
                .build();
    }
}