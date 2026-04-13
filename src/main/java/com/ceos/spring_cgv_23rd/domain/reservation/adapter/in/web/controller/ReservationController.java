package com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.controller;

import com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.dto.request.ReservationRequest;
import com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.dto.response.ReservationResponse;
import com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.mapper.ReservationRequestMapper;
import com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.mapper.ReservationResponseMapper;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationDetailResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.in.CancelReservationUseCase;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.in.CreateReservationUseCase;
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

    private final CreateReservationUseCase createReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;
    private final ReservationRequestMapper reservationRequestMapper;
    private final ReservationResponseMapper reservationResponseMapper;

    @Operation(summary = "영화 예매")
    @PostMapping
    public ApiResponse<ReservationResponse.ReservationDetailResponse> createReservation(
            @LoginUser Long userId,
            @Valid @RequestBody ReservationRequest.CreateReservationRequest request) {
        ReservationDetailResult result = createReservationUseCase.createReservation(userId, reservationRequestMapper.toCommand(request));
        ReservationResponse.ReservationDetailResponse response = reservationResponseMapper.toResponse(result);

        return ApiResponse.onSuccess("영화 예매 성공", response);
    }

    @Operation(summary = "예매 취소")
    @PatchMapping("/{reservationId}/cancel")
    public ApiResponse<Void> cancelReservation(
            @LoginUser Long userId,
            @PathVariable Long reservationId) {
        cancelReservationUseCase.cancelReservation(userId, reservationId);
        return ApiResponse.onSuccess("예매 취소 성공");
    }

    @Operation(summary = "비회원 영화 예매")
    @PostMapping("/guest")
    public ApiResponse<ReservationResponse.ReservationDetailResponse> createGuestReservation(
            @Valid @RequestBody ReservationRequest.CreateGuestReservationRequest request) {
        ReservationDetailResult result = createReservationUseCase.createGuestReservation(reservationRequestMapper.toCommand(request));
        ReservationResponse.ReservationDetailResponse response = reservationResponseMapper.toResponse(result);

        return ApiResponse.onSuccess("비회원 영화 예매 성공", response);
    }

    @Operation(summary = "비회원 예매 취소")
    @PatchMapping("/guest/{reservationId}/cancel")
    public ApiResponse<Void> cancelGuestReservation(
            @PathVariable Long reservationId,
            @Valid @RequestBody ReservationRequest.CancelGuestReservationRequest request) {
        cancelReservationUseCase.cancelGuestReservation(reservationRequestMapper.toCommand(reservationId, request));
        return ApiResponse.onSuccess("비회원 예매 취소 성공");
    }


}
