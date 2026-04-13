package com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.mapper;

import com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.dto.request.ReservationRequest;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CancelGuestReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CreateGuestReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CreateReservationCommand;
import org.springframework.stereotype.Component;

@Component
public class ReservationRequestMapper {

    public CreateReservationCommand toCommand(ReservationRequest.CreateReservationRequest request) {
        return new CreateReservationCommand(request.screeningId(), request.seatIds());
    }

    public CreateGuestReservationCommand toCommand(ReservationRequest.CreateGuestReservationRequest request) {
        return new CreateGuestReservationCommand(
                request.screeningId(),
                request.seatIds(),
                request.guestName(),
                request.guestPhone(),
                request.guestBirth(),
                request.guestPassword()
        );
    }

    public CancelGuestReservationCommand toCommand(Long reservationId, ReservationRequest.CancelGuestReservationRequest request) {
        return new CancelGuestReservationCommand(
                reservationId,
                request.guestPhone(),
                request.guestBirth(),
                request.guestPassword()
        );
    }
}
