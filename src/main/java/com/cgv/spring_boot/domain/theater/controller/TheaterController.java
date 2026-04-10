package com.cgv.spring_boot.domain.theater.controller;

import com.cgv.spring_boot.domain.theater.dto.TheaterResponse;
import com.cgv.spring_boot.domain.theater.service.TheaterService;
import com.cgv.spring_boot.global.common.response.ApiResponse;
import com.cgv.spring_boot.global.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Theater", description = "영화관(지점) 조회 관련 API")
@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    @Operation(
            summary = "전체 영화관 목록 조회",
            description = "현재 등록된 모든 영화관(지점)의 리스트를 반환합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<TheaterResponse>>> getAllTheaters() {
        return ResponseEntity.ok(ApiResponse.success(theaterService.findAllTheaters()));
    }

    @Operation(
            summary = "특정 영화관 상세 조회",
            description = "영화관 ID(PK)를 이용해 해당 지점의 상세 정보(위치, 주소 등)를 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TheaterResponse>> getTheater(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ApiResponse.success(theaterService.findTheaterById(id)));
    }

    @Operation(
            summary = "영화관 찜",
            description = "로그인한 사용자가 특정 영화관을 찜합니다."
    )
    @PostMapping("/{id}/wish")
    public ResponseEntity<ApiResponse<Long>> wishTheater(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success(theaterService.wishTheater(authenticatedUser.getUserId(), id)));
    }
}
