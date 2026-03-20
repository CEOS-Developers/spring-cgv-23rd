package com.ceos23.cgv.domain.reservation.dto;

import com.ceos23.cgv.domain.reservation.entity.ReservedSeat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservedSeatResponse {
    private Long reservedSeatId;
    private String seatRow;
    private int seatCol;
    private Long screeningId;

    public static ReservedSeatResponse from(ReservedSeat reservedSeat) {
        return ReservedSeatResponse.builder()
                .reservedSeatId(reservedSeat.getId())
                .seatRow(reservedSeat.getSeatRow())
                .seatCol(reservedSeat.getSeatCol())
                .screeningId(reservedSeat.getScreening().getId())
                .build();
    }
}