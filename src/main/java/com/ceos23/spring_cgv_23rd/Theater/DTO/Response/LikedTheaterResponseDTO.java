package com.ceos23.spring_cgv_23rd.Theater.DTO.Response;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;

public record LikedTheaterResponseDTO(
        RequestType type,
        long TheaterId, String theaterName
) {
    public static LikedTheaterResponseDTO create(RequestType type, Theater theater){
        return new LikedTheaterResponseDTO(
                type, theater.getId(), theater.getName()
        );
    }
}
