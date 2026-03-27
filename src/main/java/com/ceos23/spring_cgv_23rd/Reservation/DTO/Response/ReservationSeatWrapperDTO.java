package com.ceos23.spring_cgv_23rd.Reservation.DTO.Response;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationSeat;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;

import java.util.ArrayList;
import java.util.List;

public record ReservationSeatWrapperDTO(
        String seatName, SeatInfo seatInfo
) {
    public static ReservationSeatWrapperDTO create(ReservationSeat rs){
        return new ReservationSeatWrapperDTO(
                rs.getSeatName(), rs.getSeatInfo()
        );
    }

    public static List<ReservationSeatWrapperDTO> create(List<ReservationSeat> rss){
        List<ReservationSeatWrapperDTO> res = new ArrayList<>();

        for (ReservationSeat rs : rss){
            res.add(ReservationSeatWrapperDTO.create(rs));
        }

        return res;
    }
}
