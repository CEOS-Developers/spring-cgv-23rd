package com.cgv.spring_boot.domain.theater.dto;

import com.cgv.spring_boot.domain.theater.entity.Theater;

public record TheaterResponse(
        Long id,
        String name,
        String location,
        String address
) {
    public static TheaterResponse from(Theater theater) {
        return new TheaterResponse(
                theater.getId(),
                theater.getName(),
                theater.getLocation(),
                theater.getAddress()
        );
    }
}
