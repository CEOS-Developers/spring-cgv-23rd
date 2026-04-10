package com.ceos.spring_boot.domain.cinema.controller;

import com.ceos.spring_boot.domain.cinema.dto.CinemaCreateRequest;
import com.ceos.spring_boot.domain.cinema.dto.CinemaListResponse;
import com.ceos.spring_boot.domain.cinema.dto.CinemaResponse;
import com.ceos.spring_boot.domain.cinema.service.CinemaService;
import com.ceos.spring_boot.global.codes.SuccessCode;
import com.ceos.spring_boot.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cinema 관련 API", description = "영화관 조회 및 관리를 위한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cinemas")
public class CinemaController {

    private final CinemaService cinemaService;

    // 영화관 생성
    @PostMapping
    @Operation(summary = "영화관 생성", description = "영화관을 생성합니다.")
    public ResponseEntity<ApiResponse<CinemaResponse>> createCinema(@RequestBody @Valid CinemaCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.of(cinemaService.createCinema(request), SuccessCode.INSERT_SUCCESS));
    }

    // 모든 영화관 조회
    @GetMapping
    @Operation(summary = "모든 영화관 검색", description = "모든 영화관을 조회합니다.")
    public ResponseEntity<ApiResponse<CinemaListResponse>> getAllCinemas() {
        CinemaListResponse response = cinemaService.findAllCinemas();
        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.GET_SUCCESS));
    }

    // 영화관 id로 특정 영화관 조회
    @GetMapping("{cinemaId}")
    @Operation(summary = "특정 영화관 상세 조회", description = "영화관 ID를 이용해 개별 영화관 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<CinemaResponse>> getCinemaById(
            @Parameter(description = "조회할 영화관의 ID")
            @PathVariable Long cinemaId) {
        CinemaResponse response = cinemaService.findCinemaById(cinemaId);
        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.GET_SUCCESS));
    }

    // 영화관 삭제
    @DeleteMapping("/{cinemaId}")
    @Operation(summary = "영화관 삭제", description = "영화관 id를 이용해 영화관을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteCinema(@PathVariable Long cinemaId) {
        cinemaService.deleteCinema(cinemaId);
        return ResponseEntity.ok(ApiResponse.of(null, SuccessCode.DELETE_SUCCESS));
    }
}