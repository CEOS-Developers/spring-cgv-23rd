package com.ceos.spring_cgv_23rd.domain.reservation.controller;

import com.ceos.spring_cgv_23rd.domain.reservation.dto.ReservationRequestDTO;
import com.ceos.spring_cgv_23rd.domain.reservation.dto.ReservationResponseDTO;
import com.ceos.spring_cgv_23rd.domain.reservation.service.ReservationService;
import com.ceos.spring_cgv_23rd.global.annotation.LoginUser;
import com.ceos.spring_cgv_23rd.global.apiPayload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation", description = "영화 예매 관련 API")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "영화 예매")
    @PostMapping
    public ApiResponse<ReservationResponseDTO.ReservationDetailResponseDTO> createReservation(
            @LoginUser Long userId,
            @Valid @RequestBody ReservationRequestDTO.CreateReservationRequestDTO request) {
        ReservationResponseDTO.ReservationDetailResponseDTO response = reservationService.createReservation(userId, request);
        return ApiResponse.onSuccess("영화 예매 성공", response);
    }

    @Operation(summary = "예매 취소")
    @PatchMapping("/{reservationId}/cancel")
    public ApiResponse<Void> cancelReservation(
            @LoginUser Long userId,
            @PathVariable Long reservationId) {
        reservationService.cancelReservation(userId, reservationId);
        return ApiResponse.onSuccess("예매 취소 성공");
    }

    @Operation(summary = "비회원 영화 예매")
    @PostMapping("/guest")
    public ApiResponse<ReservationResponseDTO.ReservationDetailResponseDTO> createGuestReservation(
            @Valid @RequestBody ReservationRequestDTO.CreateGuestReservationRequestDTO request) {
        ReservationResponseDTO.ReservationDetailResponseDTO response = reservationService.createGuestReservation(request);
        return ApiResponse.onSuccess("비회원 영화 예매 성공", response);
    }

    @Operation(summary = "비회원 예매 취소")
    @PatchMapping("/guest/cancel")
    public ApiResponse<Void> cancelGuestReservation(
            @Valid @RequestBody ReservationRequestDTO.CancelGuestReservationRequestDTO request) {
        reservationService.cancelGuestReservation(request);
        return ApiResponse.onSuccess("비회원 예매 취소 성공");
    }
}
