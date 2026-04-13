package com.ceos.spring_cgv_23rd.domain.reservation.application.port.in;

import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CancelGuestReservationCommand;

public interface CancelReservationUseCase {

    void cancelReservation(Long userId, Long reservationId);

    void cancelGuestReservation(CancelGuestReservationCommand command);
}
