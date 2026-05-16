package com.ceos23.spring_boot.domain.theater.dto;

import com.ceos23.spring_boot.domain.theater.entity.Theater;

import java.io.Serializable;

public record TheaterInfo(
        Long id,
        String name,
        String location
) implements Serializable {

    public static TheaterInfo from(Theater theater) {
        return new TheaterInfo(
                theater.getId(),
                theater.getName(),
                theater.getLocation()
        );
    }
}
