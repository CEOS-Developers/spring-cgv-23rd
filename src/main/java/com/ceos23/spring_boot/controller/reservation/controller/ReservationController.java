package com.ceos23.spring_boot.controller.reservation.controller;

import com.ceos23.spring_boot.controller.payment.dto.PaymentCreateRequest;
import com.ceos23.spring_boot.controller.payment.dto.PaymentResponse;
import com.ceos23.spring_boot.controller.payment.dto.SeatReserveRequest;
import com.ceos23.spring_boot.controller.reservation.dto.ReservationCancelRequest;
import com.ceos23.spring_boot.controller.reservation.dto.ReservationCreateRequest;
import com.ceos23.spring_boot.controller.reservation.dto.ReservationResponse;
import com.ceos23.spring_boot.domain.payment.dto.PaymentDataInfo;
import com.ceos23.spring_boot.domain.reservation.dto.ReservationInfo;
import com.ceos23.spring_boot.domain.reservation.facade.ReservationLockFacade;
import com.ceos23.spring_boot.domain.reservation.service.ReservationService;
import com.ceos23.spring_boot.global.security.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reservation (예매)", description = "영화 예매 API")
@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationLockFacade reservationLockFacade;

    @Operation(summary = "좌석 선점 (5분 유지)", description = "결제 전 좌석을 5분간 PENDING 상태로 점유합니다.")
    @PostMapping("/api/reservations/seats")
    public ResponseEntity<ReservationInfo> reserveSeats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SeatReserveRequest request
    ) {
        ReservationInfo info = reservationLockFacade.createReservationWithLock(
                request.toCommand(userDetails.getEmail())
        );
        return ResponseEntity.ok(info);
    }

    @Operation(
            summary = "결제 및 예매",
            description = "선택한 좌석에 분산 락을 걸어 선점하고, 결제를 진행한 뒤 예매를 최종 확정합니다."
    )
    @PostMapping("/api/reservations/instant")
    public ResponseEntity<PaymentResponse> requestInstantPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PaymentCreateRequest request
    ) {
        PaymentDataInfo info = reservationService.requestInstantPayment(
                request.paymentId(),
                userDetails.getEmail(),
                request.toFrontendRequest()
        );

        return ResponseEntity.ok(PaymentResponse.from(info));
    }

    @Operation(
            summary = "결제 및 예매 취소",
            description = "완료된 결제를 취소하고, 좌석을 다시 예매 가능 상태로 반환합니다."
    )
    @PostMapping("/api/reservations/{paymentId}/cancel")
    public ResponseEntity<Void> cancelPayment(
            @PathVariable
            @Parameter(description = "결제 고유 ID (예매 번호)", required = true, example = "20240520_a1b2c3d4")
            String paymentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        reservationService.cancelPayment(paymentId, userDetails.getEmail());

        return ResponseEntity.ok().build();
    }
}
