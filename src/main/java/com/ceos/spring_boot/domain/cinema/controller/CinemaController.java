package com.ceos.spring_boot.domain.cinema.controller;

import com.ceos.spring_boot.domain.cinema.dto.CinemaListResponse;
import com.ceos.spring_boot.domain.cinema.dto.CinemaResponse;
import com.ceos.spring_boot.domain.cinema.service.CinemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cinema 관련 API", description = "영화관 조회 및 관리를 위한 API입니다.")
@RestController
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;

    // 모든 영화관 조회
    @GetMapping("/cinemas")
    @Operation(summary = "모든 영화관 검색", description = "모든 영화관을 조회합니다.")
    public ResponseEntity<CinemaListResponse> getAllCinemas() {
        return ResponseEntity.ok(cinemaService.findAllCinemas());
    }

    // 영화관 id로 특정 영화관 조회
    @GetMapping("/cinemas/{id}")
    @Operation(summary = "특정 영화관 상세 조회", description = "영화관 ID를 이용해 개별 영화관 정보를 조회합니다.")
    public ResponseEntity<CinemaResponse> getCinemaById(
            @Parameter(description = "조회할 영화관의 ID")
            @PathVariable Long id) {
        return ResponseEntity.ok(cinemaService.findCinemaById(id));
    }
}