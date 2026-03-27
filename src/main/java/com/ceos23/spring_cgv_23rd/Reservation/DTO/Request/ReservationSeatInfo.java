package com.ceos23.spring_cgv_23rd.Reservation.DTO.Request;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import lombok.Builder;

public record ReservationSeatInfo(
        String seatName,
        SeatInfo info
) {
    public static ReservationSeatInfo create (
            String seatName, SeatInfo seatInfo
    ) {
        return new ReservationSeatInfo(seatName, seatInfo);
    }
}
