package com.ceos23.spring_boot.domain.theater.dto;

public record TheaterUpdateCommand(
        String name,
        String location
) {
}
