package com.cgv.spring_boot.domain.reservation.controller;

import com.cgv.spring_boot.domain.reservation.dto.ReservationRequest;
import com.cgv.spring_boot.domain.reservation.service.ReservationService;
import com.cgv.spring_boot.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> reserve(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody ReservationRequest request
    ) {
        Long reservationId = reservationService.reserve(userId, request);
        return ResponseEntity.ok(ApiResponse.success(reservationId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<String>> cancel(@PathVariable("id") Long id) {
        reservationService.cancel(id);
        return ResponseEntity.ok(ApiResponse.success("예매가 성공적으로 취소되었습니다."));
    }
}
