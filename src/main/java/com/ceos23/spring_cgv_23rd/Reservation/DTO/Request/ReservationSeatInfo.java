package com.ceos23.spring_cgv_23rd.Reservation.DTO.Request;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import lombok.Builder;

@Builder
public record ReservationSeatInfo(
        String seatName,
        SeatInfo info
) {
}
