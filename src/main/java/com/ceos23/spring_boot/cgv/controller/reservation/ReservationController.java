package com.ceos23.spring_boot.cgv.controller.reservation;

import com.ceos23.spring_boot.cgv.domain.reservation.Reservation;
import com.ceos23.spring_boot.cgv.dto.reservation.ReservationCreateRequest;
import com.ceos23.spring_boot.cgv.dto.reservation.ReservationResponse;
import com.ceos23.spring_boot.cgv.global.security.CustomUserDetails;
import com.ceos23.spring_boot.cgv.service.reservation.ReservationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ReservationCreateRequest request
    ) {
        Reservation reservation = reservationService.createReservation(
                userDetails.getUserId(),
                request.screeningId(),
                request.seatTemplateIds(),
                request.paymentId()
        );

        return ReservationResponse.of(
                reservation,
                reservationService.getReservationSeats(reservation)
        );
    }

    @GetMapping
    public List<ReservationResponse> getReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return reservationService.getReservations(userDetails.getUserId()).stream()
                .map(reservation -> ReservationResponse.of(
                        reservation,
                        reservationService.getReservationSeats(reservation)
                ))
                .toList();
    }

    @GetMapping("/{reservationId}")
    public ReservationResponse getReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Reservation reservation = reservationService.getReservation(reservationId, userDetails.getUserId());

        return ReservationResponse.of(
                reservation,
                reservationService.getReservationSeats(reservation)
        );
    }

    @DeleteMapping("/{reservationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        reservationService.cancelReservation(reservationId, userDetails.getUserId());
    }
}
