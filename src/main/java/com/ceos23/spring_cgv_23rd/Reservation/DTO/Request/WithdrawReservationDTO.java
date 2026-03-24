package com.ceos23.spring_cgv_23rd.Reservation.DTO.Request;

import lombok.Builder;

@Builder
public record WithdrawReservationDTO(
        long reservationId
) {
}
