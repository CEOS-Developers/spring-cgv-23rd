package com.ceos23.spring_cgv_23rd.Reservation.DTO.Request;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public record ReservationRequestDTO(
        long screeningId,
        List<ReservationSeatInfo> seatInfos
) {
    public static ReservationRequestDTO create(long screeningId, List<ReservationSeatInfo> seatInfos){
        return new ReservationRequestDTO(
                screeningId, seatInfos);
    }

    public Map<String, SeatInfo> toReservingSeats(){
        return seatInfos().stream()
                .collect(Collectors.toMap(
                        ReservationSeatInfo::seatName,
                        ReservationSeatInfo::info
                ));
    }

    public List<SeatInfo> toSeatInfos(){
        return seatInfos().stream()
                .map(ReservationSeatInfo::info)
                .toList();
    }
}
