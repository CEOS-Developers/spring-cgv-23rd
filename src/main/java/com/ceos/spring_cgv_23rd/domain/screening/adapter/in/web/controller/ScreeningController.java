package com.ceos.spring_cgv_23rd.domain.screening.adapter.in.web.controller;

import com.ceos.spring_cgv_23rd.domain.screening.adapter.in.web.dto.response.ScreeningResponse;
import com.ceos.spring_cgv_23rd.domain.screening.adapter.in.web.mapper.ScreeningResponseMapper;
import com.ceos.spring_cgv_23rd.domain.screening.application.dto.result.ScreeningDetailResult;
import com.ceos.spring_cgv_23rd.domain.screening.application.port.in.GetScreeningScheduleUseCase;
import com.ceos.spring_cgv_23rd.global.apiPayload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/screenings")
@Tag(name = "Screening", description = "상영 일정 관련 API")
public class ScreeningController {

    private final GetScreeningScheduleUseCase getScreeningScheduleUseCase;
    private final ScreeningResponseMapper mapper;

    @Operation(summary = "영화별 상영 스케줄 조회", description = "특정 영화/극장/날짜의 상영 스케줄을 상영관별로 조회")
    @GetMapping("/by-movie")
    public ApiResponse<List<ScreeningResponse.ScreeningByMovieResponse>> getScreeningsByMovie(
            @RequestParam Long movieId,
            @RequestParam Long theaterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<ScreeningDetailResult> results = getScreeningScheduleUseCase.getScreeningByMovie(movieId, theaterId, date);
        List<ScreeningResponse.ScreeningByMovieResponse> response = mapper.toMovieResponses(results);

        return ApiResponse.onSuccess("상영 스케줄 조회 성공", response);
    }

    @Operation(summary = "극장별 상영 스케줄 조회", description = "특정 극장/날짜의 상영 스케줄을 영화별로 조회")
    @GetMapping("/by-theater")
    public ApiResponse<List<ScreeningResponse.ScreeningByTheaterResponse>> getScreeningsByTheater(
            @RequestParam Long theaterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<ScreeningDetailResult> results = getScreeningScheduleUseCase.getScreeningByTheater(theaterId, date);
        List<ScreeningResponse.ScreeningByTheaterResponse> response = mapper.toTheaterResponses(results);

        return ApiResponse.onSuccess("상영 스케줄 조회 성공", response);
    }
}
