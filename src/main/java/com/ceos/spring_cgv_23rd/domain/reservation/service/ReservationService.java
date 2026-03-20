package com.ceos.spring_cgv_23rd.domain.reservation.service;

import com.ceos.spring_cgv_23rd.domain.reservation.dto.ReservationRequestDTO;
import com.ceos.spring_cgv_23rd.domain.reservation.dto.ReservationResponseDTO;

public interface ReservationService {

    ReservationResponseDTO.ReservationDetailResponseDTO createReservation(Long userId, ReservationRequestDTO.CreateReservationRequestDTO request);

    void cancelReservation(Long userId, Long reservationId);
}
