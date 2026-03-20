package com.ceos23.spring_boot.controller.theater.controller;

import com.ceos23.spring_boot.controller.theater.dto.TheaterCreateRequest;
import com.ceos23.spring_boot.controller.theater.dto.TheaterResponse;
import com.ceos23.spring_boot.controller.theater.dto.TheaterSearchRequest;
import com.ceos23.spring_boot.controller.theater.dto.TheaterUpdateRequest;
import com.ceos23.spring_boot.domain.theater.dto.TheaterInfo;
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

@Tag(name = "1. Theater (영화관)", description = "영화관 API")
@RestController
@RequiredArgsConstructor
public class TheaterController {
    private final TheaterService theaterService;

    @Operation(summary = "영화관 목록 조회", description = "전체 영화관 목록을 조회하거나 지역으로 필터링합니다.")
    @GetMapping("/api/theaters")
    public ResponseEntity<List<TheaterResponse>> findTheaters(
            @ParameterObject
            @ModelAttribute TheaterSearchRequest request) {
        List<TheaterResponse> responses = theaterService.findTheaters(request.toCommand())
                .stream()
                .map(TheaterResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "영화관 조회", description = "아이디로 영화관을 조회합니다.")
    @GetMapping("/api/theaters/{theaterId}")
    public ResponseEntity<TheaterResponse> findTheater(
            @PathVariable Long theaterId
    ) {
        TheaterResponse response = TheaterResponse.from(theaterService.findTheater(theaterId));

        return ResponseEntity
                .ok(response);
    }

    @Operation(summary = "영화관 생성", description = "새로운 영화관을 등록합니다.")
    @PostMapping("/api/theaters")
    public ResponseEntity<TheaterResponse> createTheater(@Valid @RequestBody TheaterCreateRequest request) {
        TheaterInfo info = theaterService.createTheater(request.toCommand());
        TheaterResponse response = TheaterResponse.from(info);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "영화관 수정", description = "영화관 지점명과 지역을 수정합니다.")
    @PatchMapping("/api/theaters/{theaterId}")
    public ResponseEntity<TheaterResponse> updateTheater(
            @PathVariable Long theaterId,
            @Valid @RequestBody TheaterUpdateRequest request
            ) {
        TheaterInfo info = theaterService.updateTheater(theaterId, request.toCommand());

        TheaterResponse response = TheaterResponse.from(info);

        return ResponseEntity
                .ok(response);
    }

    @Operation(summary = "영화관 삭제", description = "영화관을 삭제합니다.")
    @DeleteMapping("/api/theaters/{theaterId}")
    public ResponseEntity<Void> deleteTheater(@PathVariable Long theaterId) {
        theaterService.deleteTheater(theaterId);

        return ResponseEntity
                .noContent()
                .build();
    }
}
