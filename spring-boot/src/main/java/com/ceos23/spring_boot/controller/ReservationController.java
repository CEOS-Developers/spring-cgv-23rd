package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.domain.Reservation;
import com.ceos23.spring_boot.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    // 🎟 예매
    @PostMapping
    public Reservation reserve(@RequestBody ReservationRequest request) {
        return reservationService.reserve(
                request.userId(),
                request.screeningId(),
                request.seatId()
        );
    }

    // ❌ 취소
    @DeleteMapping("/{id}")
    public void cancel(@PathVariable Long id) {
        reservationService.cancel(id);
    }
}