package com.ceos.spring_cgv_23rd.domain.reservation.application.port.in;

import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CreateReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationResult;

public interface CreateReservationUseCase {

    ReservationResult createReservation(Long userId, CreateReservationCommand command);
}
