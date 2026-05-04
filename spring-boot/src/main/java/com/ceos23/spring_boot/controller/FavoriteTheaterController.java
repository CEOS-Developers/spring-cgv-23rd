package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.dto.FavoriteTheaterResponse;
import com.ceos23.spring_boot.global.response.SuccessResponse;
import com.ceos23.spring_boot.service.FavoriteTheaterService;
import com.ceos23.spring_boot.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite-theaters")
public class FavoriteTheaterController {

    private final FavoriteTheaterService favoriteTheaterService;

    @PostMapping("/{theaterId}")
    public ResponseEntity<SuccessResponse<FavoriteTheaterResponse>> createFavoriteTheater(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long theaterId
    ) {
        Long userId = userDetails.getUserId();

        FavoriteTheaterResponse response = favoriteTheaterService.createFavoriteTheater(userId, theaterId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }

    @DeleteMapping("/{theaterId}")
    public ResponseEntity<SuccessResponse<Void>> deleteFavoriteTheater(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long theaterId
    ) {
        Long userId = userDetails.getUserId();

        favoriteTheaterService.deleteFavoriteTheater(userId, theaterId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", null));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<FavoriteTheaterResponse>>> getFavoriteTheaters(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        List<FavoriteTheaterResponse> response = favoriteTheaterService.getFavoriteTheaters(userId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }
}