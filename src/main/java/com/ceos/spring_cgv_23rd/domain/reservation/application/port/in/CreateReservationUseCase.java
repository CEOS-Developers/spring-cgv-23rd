package com.ceos.spring_cgv_23rd.domain.reservation.application.port.in;

import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CreateGuestReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CreateReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationDetailResult;

public interface CreateReservationUseCase {

    ReservationDetailResult createReservation(Long userId, CreateReservationCommand command);

    ReservationDetailResult createGuestReservation(CreateGuestReservationCommand command);
}
