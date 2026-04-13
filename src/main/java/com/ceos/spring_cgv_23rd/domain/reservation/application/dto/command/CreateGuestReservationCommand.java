package com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command;

import java.time.LocalDate;
import java.util.List;

public record CreateGuestReservationCommand(
        Long screeningId,
        List<Long> seatIds,
        String guestName,
        String guestPhone,
        LocalDate guestBirth,
        String guestPassword
) {
}
