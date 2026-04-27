package com.ceos.spring_cgv_23rd.domain.theater.adapter.in.web.controller;

import com.ceos.spring_cgv_23rd.domain.theater.adapter.in.web.dto.response.TheaterResponse;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.in.web.mapper.TheaterRequestMapper;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.in.web.mapper.TheaterResponseMapper;
import com.ceos.spring_cgv_23rd.domain.theater.application.dto.command.ToggleTheaterLikeCommand;
import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.ToggleTheaterLikeResult;
import com.ceos.spring_cgv_23rd.domain.theater.application.port.in.GetTheaterUseCase;
import com.ceos.spring_cgv_23rd.domain.theater.application.port.in.ToggleTheaterLikeUseCase;
import com.ceos.spring_cgv_23rd.domain.theater.domain.Theater;
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

    private final GetTheaterUseCase getTheaterUseCase;
    private final ToggleTheaterLikeUseCase toggleTheaterLikeUseCase;
    private final TheaterRequestMapper requestMapper;
    private final TheaterResponseMapper responseMapper;

    @Operation(summary = "영화관 목록 조회")
    @GetMapping
    public ApiResponse<List<TheaterResponse.TheaterListResponse>> getTheaters() {
        List<Theater> theaters = getTheaterUseCase.getTheaters();
        List<TheaterResponse.TheaterListResponse> response = responseMapper.toTheaterListResponse(theaters);

        return ApiResponse.onSuccess("영화관 목록 조회 성공", response);
    }

    @Operation(summary = "영화관 상세 조회")
    @GetMapping("/{theaterId}")
    public ApiResponse<TheaterResponse.TheaterDetailResponse> getTheaterDetail(
            @PathVariable Long theaterId) {
        Theater theater = getTheaterUseCase.getTheaterDetail(theaterId);
        TheaterResponse.TheaterDetailResponse response = responseMapper.toTheaterDetailResponse(theater);

        return ApiResponse.onSuccess("영화관 상세 조회 성공", response);
    }

    @Operation(summary = "영화관 찜 토글")
    @PostMapping("/{theaterId}/like")
    public ApiResponse<TheaterResponse.TheaterLikeResponse> toggleTheaterLike(
            @LoginUser Long userId,
            @PathVariable Long theaterId) {
        ToggleTheaterLikeCommand command = requestMapper.toggleTheaterLikeCommand(userId, theaterId);
        ToggleTheaterLikeResult result = toggleTheaterLikeUseCase.execute(command);
        String message = result.liked() ? "영화관 찜 등록 성공" : "영화관 찜 취소 성공";
        TheaterResponse.TheaterLikeResponse response = responseMapper.toTheaterLikeResponse(result);

        return ApiResponse.onSuccess(message, response);
    }
}
