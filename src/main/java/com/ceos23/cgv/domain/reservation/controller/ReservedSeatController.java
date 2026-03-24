package com.ceos23.cgv.domain.reservation.controller;

import com.ceos23.cgv.domain.reservation.dto.ReservedSeatRequest;
import com.ceos23.cgv.domain.reservation.dto.ReservedSeatResponse;
import com.ceos23.cgv.domain.reservation.service.ReservedSeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reserved-seats")
@RequiredArgsConstructor
@Tag(name = "Reserved Seat API", description = "예매 좌석 선점 및 조회 API")
public class ReservedSeatController {

    private final ReservedSeatService reservedSeatService;

    @PostMapping
    @Operation(summary = "좌석 예매(선점)", description = "선택한 좌석 행(Row)과 열(Col)을 저장합니다. 이미 예약된 좌석일 경우 예외가 발생합니다.")
    public ResponseEntity<List<ReservedSeatResponse>> createReservedSeats(@RequestBody ReservedSeatRequest request) {
        List<ReservedSeatResponse> responses = reservedSeatService.createReservedSeats(request).stream()
                .map(ReservedSeatResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping("/screening/{screeningId}")
    @Operation(summary = "예매 완료된 좌석 조회", description = "특정 상영 시간표(Screening)에 이미 예매가 끝난 좌석 행/열 목록을 불러옵니다.")
    public ResponseEntity<List<ReservedSeatResponse>> getReservedSeats(@PathVariable Long screeningId) {
        List<ReservedSeatResponse> responses = reservedSeatService.getReservedSeatsByScreeningId(screeningId).stream()
                .map(ReservedSeatResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}