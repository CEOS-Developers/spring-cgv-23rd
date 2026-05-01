package com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command;

import java.util.List;

public record ConfirmReservationCommand(
        String paymentId,
        Long userId,
        Long screeningId,
        List<Long> seatIds
) {
}
