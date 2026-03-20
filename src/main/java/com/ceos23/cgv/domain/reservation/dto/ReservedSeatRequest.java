package com.ceos23.cgv.domain.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReservedSeatRequest {
    private Long reservationId;
    private Long screeningId;
    private List<SeatInfo> seats;

    @Getter
    @NoArgsConstructor
    public static class SeatInfo {
        private String row; // 예: "H"
        private int col;    // 예: 9
    }
}