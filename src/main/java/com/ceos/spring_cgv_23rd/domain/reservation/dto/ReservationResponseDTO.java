package com.ceos.spring_cgv_23rd.domain.reservation.dto;

import com.ceos.spring_cgv_23rd.domain.reservation.entity.Reservation;
import com.ceos.spring_cgv_23rd.domain.reservation.entity.ReservationSeat;
import com.ceos.spring_cgv_23rd.domain.reservation.enums.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationResponseDTO {

    @Builder
    public record ReservationDetailResponseDTO(
            Long reservationId,
            String reservationNumber,
            ReservationStatus status,
            String movieTitle,
            String theaterName,
            String hallName,
            LocalDateTime startAt,
            LocalDateTime endAt,
            List<SeatInfoDTO> seats,
            Integer totalPrice,
            LocalDateTime createdAt
    ) {
        public static ReservationDetailResponseDTO of(Reservation reservation, List<ReservationSeat> reservationSeats) {
            var screening = reservation.getScreeningEntity();
            var hall = screening.getHall();

            return ReservationDetailResponseDTO.builder()
                    .reservationId(reservation.getId())
                    .reservationNumber(reservation.getReservationNumber())
                    .status(reservation.getStatus())
                    .movieTitle(screening.getMovie().getTitle())
                    .theaterName(hall.getTheater().getName())
                    .hallName(hall.getName())
                    .startAt(screening.getStartAt())
                    .endAt(screening.getEndAt())
                    .seats(reservationSeats.stream()
                            .map(SeatInfoDTO::from)
                            .toList())
                    .totalPrice(reservation.getTotalPrice())
                    .createdAt(reservation.getCreatedAt())
                    .build();
        }
    }

    @Builder
    private record SeatInfoDTO(
            Long seatId,
            Integer rowNum,
            Integer colNum
    ) {
        private static SeatInfoDTO from(ReservationSeat rs) {
            return SeatInfoDTO.builder()
                    .seatId(rs.getSeat().getId())
                    .rowNum(rs.getSeat().getRowNum())
                    .colNum(rs.getSeat().getColNum())
                    .build();
        }
    }
}
