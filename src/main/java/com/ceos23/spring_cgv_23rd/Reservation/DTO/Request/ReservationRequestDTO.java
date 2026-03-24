package com.ceos23.spring_cgv_23rd.Reservation.DTO.Request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReservationRequestDTO(
        long userId,
        long screeningId,
        LocalDateTime reservationDate,
        int totalPrice,
        List<ReservationSeatInfo> seatInfos
) {
}
