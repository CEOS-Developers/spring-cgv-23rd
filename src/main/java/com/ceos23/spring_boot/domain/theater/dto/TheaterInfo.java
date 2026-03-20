package com.ceos23.spring_boot.domain.theater.dto;

import com.ceos23.spring_boot.domain.theater.entity.Theater;

public record TheaterInfo(
        Long id,
        String name,
        String location
) {

    public static TheaterInfo from(Theater theater) {
        return new TheaterInfo(
                theater.getId(),
                theater.getName(),
                theater.getLocation()
        );
    }
}
