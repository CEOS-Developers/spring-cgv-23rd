package com.ceos23.spring_boot.controller.theater.controller;

import com.ceos23.spring_boot.controller.theater.dto.*;
import com.ceos23.spring_boot.domain.theater.dto.ScreenInfo;
import com.ceos23.spring_boot.domain.theater.dto.TheaterInfo;
import com.ceos23.spring_boot.domain.theater.service.ScreenService;
import com.ceos23.spring_boot.domain.theater.service.TheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Screen (상영관)", description = "상영관 API")
@RestController
@RequiredArgsConstructor
public class ScreenController {
    private final ScreenService screenService;

    @Operation(summary = "상영관 생성", description = "새로운 상영관을 등록합니다.")
    @PostMapping("/api/screens")
    public ResponseEntity<ScreenCreateResponse> createScreenWithSeats(@Valid @RequestBody ScreenCreateRequest request) {
        ScreenInfo info = screenService.createScreenWithSeats(request.toCommand());
        ScreenCreateResponse response = ScreenCreateResponse.from(info);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}