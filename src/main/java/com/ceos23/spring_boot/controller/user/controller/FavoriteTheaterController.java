package com.ceos23.spring_boot.controller.user.controller;

import com.ceos23.spring_boot.controller.user.dto.FavoriteTheaterDto.FavoriteTheaterResponse;
import com.ceos23.spring_boot.controller.user.dto.FavoriteTheaterDto.FavoriteTheaterToggleResponse;
import com.ceos23.spring_boot.domain.user.service.FavoriteTheaterService;
import com.ceos23.spring_boot.global.security.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "FavoriteTheater (영화관 찜)", description = "영화관 찜 API")
@RestController
@RequiredArgsConstructor
public class FavoriteTheaterController {
    private final FavoriteTheaterService favoriteTheaterService;

    @Operation(summary = "찜한 영화관 목록 조회", description = "찜한 전체 영화관 목록을 조회합니다.")
    @GetMapping("/api/favoriteTheaters")
    public ResponseEntity<List<FavoriteTheaterResponse>> findFavoriteTheaters(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ) {
        List<FavoriteTheaterResponse> responses = favoriteTheaterService.findFavoriteTheaters(customUserDetails.getEmail())
                .stream()
                .map(FavoriteTheaterResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "영화관 찜 토클 (찜하기/취소하기)")
    @PostMapping("api/theaters/{theaterId}/favorites")
    public ResponseEntity<FavoriteTheaterToggleResponse> toggleFavorite(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long theaterId
    ) {
        boolean isFavorited = favoriteTheaterService.toggleFavorite(customUserDetails.getEmail(), theaterId);

        return ResponseEntity.ok(FavoriteTheaterToggleResponse.from(isFavorited));
    }
}
