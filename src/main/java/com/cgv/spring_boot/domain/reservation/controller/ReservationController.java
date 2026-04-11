package com.cgv.spring_boot.domain.reservation.controller;

import com.cgv.spring_boot.domain.reservation.dto.ReservationRequest;
import com.cgv.spring_boot.domain.reservation.service.ReservationService;
import com.cgv.spring_boot.global.common.code.SuccessCode;
import com.cgv.spring_boot.global.common.response.ApiResponse;
import com.cgv.spring_boot.global.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reservation", description = "예매 관련 API")
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "영화 예매", description = "스케줄과 좌석 정보를 통해 영화를 예매합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> reserve(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody ReservationRequest request
    ) {
        Long reservationId = reservationService.reserve(authenticatedUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(reservationId));
    }

    @Operation(summary = "영화 예매 취소", description = "영화 예매를 취소합니다.")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<String>> cancel(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("id") Long id
    ) {

        reservationService.cancel(authenticatedUser.getUserId(), id);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.DELETE_SUCCESS, "예매가 성공적으로 취소되었습니다."));
    }
}
