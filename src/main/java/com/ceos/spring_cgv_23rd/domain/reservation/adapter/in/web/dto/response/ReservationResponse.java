package com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.dto.response;

import com.ceos.spring_cgv_23rd.domain.reservation.domain.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationResponse {

    @Builder
    public record ReservationDetailResponse(
            Long reservationId,
            String reservationNumber,
            ReservationStatus status,
            String movieTitle,
            String theaterName,
            String hallName,
            LocalDateTime startAt,
            LocalDateTime endAt,
            List<SeatInfo> seats,
            Integer totalPrice,
            LocalDateTime createdAt
    ) {
    }

    @Builder
    public record SeatInfo(
            Long seatId,
            Integer rowNum,
            Integer colNum
    ) {
    }
}
