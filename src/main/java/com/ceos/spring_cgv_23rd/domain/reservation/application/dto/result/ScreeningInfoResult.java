package com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result;

import java.time.LocalDateTime;

public record ScreeningInfoResult(
        Long hallTypeId,
        String movieTitle,
        String theaterName,
        String hallName,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Integer price
) {
}
