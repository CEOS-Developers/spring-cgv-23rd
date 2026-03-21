package com.ceos23.spring_boot.cgv.dto.cinema;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;

public record CinemaResponse(
        Long cinemaId,
        String name,
        String address
) {
    public static CinemaResponse from(Cinema cinema) {
        return new CinemaResponse(
                cinema.getId(),
                cinema.getName(),
                cinema.getAddress()
        );
    }
}