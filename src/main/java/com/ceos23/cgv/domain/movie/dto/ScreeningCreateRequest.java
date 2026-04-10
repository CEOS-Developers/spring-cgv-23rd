package com.ceos23.cgv.domain.movie.dto;

import java.time.LocalDateTime;

public record ScreeningCreateRequest(
        Long movieId,
        Long theaterId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Boolean isMorning
) {
}