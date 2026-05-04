package com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.controller;

import com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.dto.request.ReservationRequest;
import com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.dto.response.ReservationResponse;
import com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.mapper.ReservationRequestMapper;
import com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.mapper.ReservationResponseMapper;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationDetailResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.in.CancelReservationUseCase;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.in.ConfirmReservationUseCase;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.in.CreateReservationUseCase;
import com.ceos.spring_cgv_23rd.global.annotation.LoginUser;
import com.ceos.spring_cgv_23rd.global.apiPayload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation", description = "영화 예매 관련 API")
public class ReservationController {

    private final CreateReservationUseCase createReservationUseCase;
    private final ConfirmReservationUseCase confirmReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;
    private final ReservationRequestMapper reservationRequestMapper;
    private final ReservationResponseMapper reservationResponseMapper;


    @Operation(summary = "영화 예매 (좌석 점유)")
    @PostMapping
    public ApiResponse<ReservationResponse.CreateReservationResponse> createReservation(
            @LoginUser Long userId,
            @Valid @RequestBody ReservationRequest.CreateReservationRequest request) {
        ReservationResult result = createReservationUseCase.createReservation(userId, reservationRequestMapper.toCommand(request));
        ReservationResponse.CreateReservationResponse response = reservationResponseMapper.toResponse(result);

        return ApiResponse.onSuccess("좌석 점유 성공", response);
    }

    @Operation(summary = "예매 확정 (결제 포함)")
    @PostMapping("/confirm")
    public ApiResponse<ReservationResponse.ReservationDetailResponse> confirmReservation(
            @LoginUser Long userId,
            @RequestHeader(value = "Idempotency-Key") @NotBlank String idempotencyKey,
            @Valid @RequestBody ReservationRequest.ConfirmReservationRequest request) {
        ReservationDetailResult result = confirmReservationUseCase.confirmReservation(reservationRequestMapper.toCommand(userId, idempotencyKey, request));
        ReservationResponse.ReservationDetailResponse response = reservationResponseMapper.toResponse(result);

        return ApiResponse.onSuccess("예매 확정 성공", response);
    }

    @Operation(summary = "예매 취소")
    @PatchMapping("/{reservationId}/cancel")
    public ApiResponse<Void> cancelReservation(
            @LoginUser Long userId,
            @PathVariable Long reservationId) {
        cancelReservationUseCase.cancel(userId, reservationId);

        return ApiResponse.onSuccess("예매 취소 성공");
    }


}
