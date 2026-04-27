package com.ceos.spring_cgv_23rd.domain.reservation.application.port.in;

import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.ConfirmReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationDetailResult;



public interface ConfirmReservationUseCase {

    ReservationDetailResult confirmReservation(ConfirmReservationCommand command);
}
