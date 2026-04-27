package com.ceos.spring_cgv_23rd.domain.reservation.application.port.in;

public interface CancelReservationUseCase {

    void cancel(Long userId, Long reservationId);
}
