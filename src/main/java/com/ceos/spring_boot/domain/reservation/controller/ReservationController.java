package com.ceos.spring_boot.domain.reservation.controller;

import com.ceos.spring_boot.domain.reservation.dto.ReservationRequest;
import com.ceos.spring_boot.domain.reservation.dto.ReservationResponse;
import com.ceos.spring_boot.domain.reservation.service.ReservationService;
import com.ceos.spring_boot.global.codes.SuccessCode;
import com.ceos.spring_boot.global.response.ApiResponse;
import com.ceos.spring_boot.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reservation 관련 API", description = "예매 및 취소를 위한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "영화 예매하기", description = "사용자, 상영 일정, 좌석 정보를 받아 예매를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ReservationRequest request
    ) {
        ReservationResponse response = reservationService.createReservation(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.INSERT_SUCCESS));
    }

    @Operation(summary = "예매 취소하기", description = "예매 ID를 이용해 예매를 취소합니다.")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);

        return ResponseEntity.ok(ApiResponse.of(null, SuccessCode.DELETE_SUCCESS));
    }
}