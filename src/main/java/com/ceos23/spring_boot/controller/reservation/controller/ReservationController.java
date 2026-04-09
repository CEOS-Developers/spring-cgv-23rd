package com.ceos23.spring_boot.controller.reservation.controller;

import com.ceos23.spring_boot.controller.reservation.dto.ReservationCancelRequest;
import com.ceos23.spring_boot.controller.reservation.dto.ReservationCreateRequest;
import com.ceos23.spring_boot.controller.reservation.dto.ReservationResponse;
import com.ceos23.spring_boot.domain.reservation.dto.ReservationInfo;
import com.ceos23.spring_boot.domain.reservation.facade.ReservationLockFacade;
import com.ceos23.spring_boot.domain.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@Tag(name = "Reservation (예매)", description = "영화 예매 API")
//@RestController
//@RequiredArgsConstructor
//public class ReservationController {
//    private final ReservationService reservationService;
//    private final ReservationLockFacade reservationLockFacade;
//
//    @Operation(summary = "영화 예매 생성", description = "상영일정과 좌석들을 선택하여 예매를 진행합니다.")
//    @PostMapping("/api/reservations")
//    public ResponseEntity<ReservationResponse> createReservation(
//           @Valid @RequestBody ReservationCreateRequest request
//    ) {
//        ReservationInfo info = reservationLockFacade.createReservationWithLock(request.toCommand());
//
//        ReservationResponse response = ReservationResponse.from(info);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(response);
//    }
//}
