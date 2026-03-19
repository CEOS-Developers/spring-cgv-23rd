package com.ceos23.cgv_clone.reservation.controller;

import com.ceos23.cgv_clone.common.ApiResponse;
import com.ceos23.cgv_clone.reservation.dto.request.ReservationRequest;
import com.ceos23.cgv_clone.reservation.dto.response.ReservationResponse;
import com.ceos23.cgv_clone.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ApiResponse<ReservationResponse> createReservation(
            @RequestHeader Long userId,
            @Valid @RequestBody ReservationRequest request
    ) {
        return reservationService.createReservation(userId, request);
    }

    @DeleteMapping("/{reservationId}")
    public ApiResponse<Void> cancelReservation(
            @RequestHeader Long userId,
            @PathVariable Long reservationId
    ) {
        return reservationService.cancelReservation(userId, reservationId);
    }
}
