package com.ceos.spring_cgv_23rd.domain.theater.controller;

import com.ceos.spring_cgv_23rd.domain.theater.application.service.TheaterService;
import com.ceos.spring_cgv_23rd.domain.theater.dto.TheaterResponseDTO;
import com.ceos.spring_cgv_23rd.global.annotation.LoginUser;
import com.ceos.spring_cgv_23rd.global.apiPayload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/theaters")
@Tag(name = "Theater", description = "영화관 관련 API")
public class TheaterController {

    private final TheaterService theaterService;

    @Operation(summary = "영화관 목록 조회")
    @GetMapping
    public ApiResponse<List<TheaterResponseDTO.TheaterListResponseDTO>> getTheaters() {
        List<TheaterResponseDTO.TheaterListResponseDTO> response = theaterService.getTheaterList();
        return ApiResponse.onSuccess("영화관 목록 조회 성공", response);
    }

    @Operation(summary = "영화관 상세 조회")
    @GetMapping("/{theaterId}")
    public ApiResponse<TheaterResponseDTO.TheaterDetailResponseDTO> getTheaterDetail(
            @PathVariable Long theaterId) {
        TheaterResponseDTO.TheaterDetailResponseDTO response = theaterService.getTheaterDetail(theaterId);
        return ApiResponse.onSuccess("영화관 상세 조회 성공", response);
    }

    @Operation(summary = "영화관 찜 토글")
    @PostMapping("/{theaterId}/like")
    public ApiResponse<TheaterResponseDTO.TheaterLikeResponseDTO> toggleTheaterLike(
            @LoginUser Long userId,
            @PathVariable Long theaterId) {
        TheaterResponseDTO.TheaterLikeResponseDTO response = theaterService.toggleTheaterLike(userId, theaterId);
        String message = response.isLiked() ? "영화관 찜 등록 성공" : "영화관 찜 취소 성공";
        return ApiResponse.onSuccess(message, response);
    }
}
