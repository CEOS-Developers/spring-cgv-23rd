package com.ceos23.spring_cgv_23rd.Reservation.DTO.Response;

import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationType;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import lombok.Builder;
import lombok.Data;


@Builder
public record ReservationResponseDTO(
        long id
) {
}
