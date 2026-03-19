package com.ceos23.cgv_clone.theater.controller;

import com.ceos23.cgv_clone.common.ApiResponse;
import com.ceos23.cgv_clone.theater.dto.response.TheaterResponse;
import com.ceos23.cgv_clone.theater.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/theaters")
public class TheaterController {

    private final TheaterService theaterService;

    @GetMapping("/{theatersId}")
    public ApiResponse<TheaterResponse> getTheater(@PathVariable Long theaterId) {

    }
}
