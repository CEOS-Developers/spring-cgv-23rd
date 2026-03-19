package com.cgv.spring_boot.domain.theater.controller;

import com.cgv.spring_boot.domain.theater.dto.TheaterResponse;
import com.cgv.spring_boot.domain.theater.service.TheaterService;
import com.cgv.spring_boot.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TheaterResponse>>> getAllTheaters() {
        return ResponseEntity.ok(ApiResponse.success(theaterService.findAllTheaters()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TheaterResponse>> getTheater(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ApiResponse.success(theaterService.findTheaterById(id)));
    }
}
