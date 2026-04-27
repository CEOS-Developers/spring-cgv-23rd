package com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result;

public record SeatInfoResult(
        Long seatId,
        Integer rowNum,
        Integer colNum
) {
}
