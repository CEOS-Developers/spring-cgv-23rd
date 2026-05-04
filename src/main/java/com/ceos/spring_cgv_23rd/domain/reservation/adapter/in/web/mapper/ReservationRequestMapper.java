package com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.mapper;

import com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.dto.request.ReservationRequest;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.ConfirmReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CreateReservationCommand;
import org.springframework.stereotype.Component;

@Component
public class ReservationRequestMapper {

    public CreateReservationCommand toCommand(ReservationRequest.CreateReservationRequest request) {
        return new CreateReservationCommand(
                request.screeningId(),
                request.seatIds()
        );
    }

    public ConfirmReservationCommand toCommand(Long userId, String idempotencyKey, ReservationRequest.ConfirmReservationRequest request) {
        return new ConfirmReservationCommand(
                idempotencyKey,
                userId,
                request.screeningId(),
                request.seatIds()
        );
    }
}
