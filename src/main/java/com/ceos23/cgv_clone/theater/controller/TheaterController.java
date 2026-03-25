package com.ceos23.cgv_clone.theater.controller;

import com.ceos23.cgv_clone.global.response.ApiResponse;
import com.ceos23.cgv_clone.global.response.SuccessCode;
import com.ceos23.cgv_clone.theater.dto.response.TheaterResponse;
import com.ceos23.cgv_clone.theater.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/theaters")
public class TheaterController {

    private final TheaterService theaterService;

    @GetMapping("/{theaterId}")
    public ApiResponse<TheaterResponse> getTheater(
            @PathVariable Long theaterId
    ) {
        return ApiResponse.ok(SuccessCode.SELECT_SUCCESS, theaterService.getTheater(theaterId));
    }

    // required = false -> 전체 조회
    @GetMapping
    public ApiResponse<List<TheaterResponse>> getTheaterByRegion(
            @RequestParam(required = false) String region
    ) {
        return ApiResponse.ok(SuccessCode.SELECT_SUCCESS, theaterService.getTheatersByRegion(region));
    }
}
