package com.ceos23.cgv_clone.reservation.dto.response;

import com.ceos23.cgv_clone.reservation.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class PendingReservationResponse {
    private Long reservationId;
    private String orderName;
    private int totalPrice;
    private LocalDateTime reservedAt;
    private List<String> reservedSeats;
    private String movieName;

    public static PendingReservationResponse from(Reservation reservation) {
        List<String> seats = reservation.getReservationSeats().stream()
                .map(s -> s.getSeatRow() + String.valueOf(s.getSeatCol()))
                .toList();

        return PendingReservationResponse.builder()
                .reservationId(reservation.getId())
                .orderName(reservation.getSchedule().getMovie().getName() + " " + String.join(", ", seats))
                .totalPrice(reservation.getTotalPrice())
                .reservedSeats(seats)
                .movieName(reservation.getSchedule().getMovie().getName())
                .build();
    }

}
