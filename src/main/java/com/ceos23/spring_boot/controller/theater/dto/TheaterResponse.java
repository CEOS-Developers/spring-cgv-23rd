package com.ceos23.spring_boot.controller.theater.dto;

import com.ceos23.spring_boot.domain.theater.dto.TheaterInfo;
import com.ceos23.spring_boot.domain.theater.entity.Theater;

public record TheaterResponse(
        Long id,
        String name,
        String location) {

    public static TheaterResponse from(TheaterInfo theaterInfo) {
        return new TheaterResponse(
                theaterInfo.id(),
                theaterInfo.name(),
                theaterInfo.location()
        );
    }
}
