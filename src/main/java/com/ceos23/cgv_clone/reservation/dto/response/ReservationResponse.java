package com.ceos23.cgv_clone.reservation.dto.response;

import com.ceos23.cgv_clone.reservation.entity.Reservation;
import com.ceos23.cgv_clone.reservation.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReservationResponse {
    private Long id;
    private LocalDateTime reservedAt;
    private int totalPrice;
    private ReservationStatus reservationStatus;

    private String movieName;
    private String theaterName;
    private String screenName;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private List<String> reservedSeats;

    public static ReservationResponse from(Reservation reservation) {

        // A + 1 => A1 로 저장
        List<String> seatNames = reservation.getReservationSeats().stream()
                .map(seat -> seat.getSeatRow() + String.valueOf(seat.getSeatCol()))
                .toList();

        return ReservationResponse.builder()
                .id(reservation.getId())
                .reservedAt(reservation.getReservedAt())
                .totalPrice(reservation.getTotalPrice())
                .reservationStatus(reservation.getStatus())
                .movieName(reservation.getSchedule().getMovie().getName())
                .theaterName(reservation.getSchedule().getScreen().getTheater().getName())
                .screenName(reservation.getSchedule().getScreen().getName())
                .startAt(reservation.getSchedule().getStartAt())
                .endAt(reservation.getSchedule().getEndAt())
                .reservedSeats(seatNames)
                .build();
    }


}
