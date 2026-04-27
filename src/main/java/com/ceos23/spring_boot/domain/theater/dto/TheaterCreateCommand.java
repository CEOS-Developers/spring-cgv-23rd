package com.ceos23.spring_boot.domain.theater.dto;

public record TheaterCreateCommand(
        String name,
        String location
) {
}
