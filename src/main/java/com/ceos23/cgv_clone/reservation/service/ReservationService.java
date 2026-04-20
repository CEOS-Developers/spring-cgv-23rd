package com.ceos23.cgv_clone.reservation.service;

import com.ceos23.cgv_clone.reservation.dto.request.ReservationRequest;
import com.ceos23.cgv_clone.reservation.dto.response.ReservationResponse;

public interface ReservationService {

    ReservationResponse createReservation(Long userId, ReservationRequest request);

    void cancelReservation(Long userId, Long reservationId);
}
