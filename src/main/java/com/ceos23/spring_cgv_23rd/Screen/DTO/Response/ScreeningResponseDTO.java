package com.ceos23.spring_cgv_23rd.Screen.DTO.Response;

import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;

import java.time.LocalDateTime;

public record ScreeningResponseDTO(
        LocalDateTime reservationTime,
        int totalPrice,
        Screening screening
) {
}
