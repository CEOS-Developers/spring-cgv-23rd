package com.ceos23.cgv_clone.reservation.controller;

import com.ceos23.cgv_clone.global.response.ApiResponse;
import com.ceos23.cgv_clone.global.response.SuccessCode;
import com.ceos23.cgv_clone.reservation.dto.request.ReservationRequest;
import com.ceos23.cgv_clone.reservation.dto.response.PendingReservationResponse;
import com.ceos23.cgv_clone.reservation.dto.response.ReservationResponse;
import com.ceos23.cgv_clone.global.jwt.CustomUserDetails;
import com.ceos23.cgv_clone.reservation.service.ReservationServiceNamed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationServiceNamed reservationServiceNamed;

    @PostMapping
    public ApiResponse<PendingReservationResponse> prepareReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReservationRequest request
    ) {
        return ApiResponse.ok(SuccessCode.INSERT_SUCCESS, reservationServiceNamed.prepareReservation(userDetails.getUserId(), request));
    }

    @PostMapping("/{reservationId}/pay")
    public ApiResponse<ReservationResponse> pay(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reservationId
    ) {
        return ApiResponse.ok(SuccessCode.UPDATE_SUCCESS, reservationServiceNamed.confirmReservation(userDetails.getUserId(), reservationId));
    }

    @DeleteMapping("/{reservationId}")
    public ApiResponse<Void> cancelReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reservationId
    ) {
        reservationServiceNamed.cancelReservation(userDetails.getUserId(), reservationId);
        return ApiResponse.ok(SuccessCode.DELETE_SUCCESS);
    }
}
