package com.ceos23.cgv.domain.cinema.dto;

import com.ceos23.cgv.domain.cinema.enums.Region;

public record CinemaCreateRequest(
        String name,
        Region region
) {
}