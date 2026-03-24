package com.ceos.spring_cgv_23rd.domain.reservation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

public class ReservationRequestDTO {

    @Builder
    public record CreateReservationRequestDTO(
            @NotNull Long screeningId,
            @NotEmpty List<Long> seatIds
    ) {
    }
}
