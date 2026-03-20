package com.ceos23.spring_boot.domain.movie.dto;

import java.time.LocalDate;

public record MovieCreateCommand(
        String title,
        Integer runtime,
        LocalDate releaseDate,
        String ageRating,
        String posterUrl,
        String description
) {
}
