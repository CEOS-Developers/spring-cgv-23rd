package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.dto.FavoriteTheaterResponse;
import com.ceos23.spring_boot.global.response.SuccessResponse;
import com.ceos23.spring_boot.service.FavoriteTheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite-theaters")
public class FavoriteTheaterController {

    private final FavoriteTheaterService favoriteTheaterService;

    @PostMapping("/{theaterId}")
    public ResponseEntity<SuccessResponse<FavoriteTheaterResponse>> createFavoriteTheater(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long theaterId
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());

        FavoriteTheaterResponse response = favoriteTheaterService.createFavoriteTheater(userId, theaterId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }

    @DeleteMapping("/{theaterId}")
    public ResponseEntity<SuccessResponse<Void>> deleteFavoriteTheater(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long theaterId
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());

        favoriteTheaterService.deleteFavoriteTheater(userId, theaterId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", null));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<FavoriteTheaterResponse>>> getFavoriteTheaters(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());

        List<FavoriteTheaterResponse> response = favoriteTheaterService.getFavoriteTheaters(userId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }
}