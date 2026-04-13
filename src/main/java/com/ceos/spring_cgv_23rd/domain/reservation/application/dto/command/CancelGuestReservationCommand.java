package com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command;

import java.time.LocalDate;

public record CancelGuestReservationCommand(
        Long reservationId,
        String guestPhone,
        LocalDate guestBirth,
        String guestPassword
) {
}
