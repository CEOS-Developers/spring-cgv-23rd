package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.domain.Reservation;
import com.ceos23.spring_boot.dto.ReservationRequest;
import com.ceos23.spring_boot.dto.ReservationResponse;
import com.ceos23.spring_boot.global.response.SuccessResponse;
import com.ceos23.spring_boot.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<SuccessResponse<ReservationResponse>> reserve(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReservationRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());

        Reservation reservation = reservationService.reserve(
                userId,
                request.screeningId(),
                request.seatId()
        );

        ReservationResponse response = ReservationResponse.from(reservation);

        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<SuccessResponse<ReservationResponse>> payReservation(
            @PathVariable @Positive(message = "예매 ID는 양수여야 합니다.") Long id
    ) {
        Reservation reservation = reservationService.payReservation(id);
        ReservationResponse response = ReservationResponse.from(reservation);

        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> cancel(
            @PathVariable @Positive(message = "예매 ID는 양수여야 합니다.") Long id
    ) {
        reservationService.cancel(id);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", null));
    }
}