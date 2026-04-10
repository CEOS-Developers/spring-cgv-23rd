package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.dto.FavoriteTheaterResponse;
import com.ceos23.spring_boot.global.response.SuccessResponse;
import com.ceos23.spring_boot.service.FavoriteTheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/favorite-theaters")
public class FavoriteTheaterController {

    private final FavoriteTheaterService favoriteTheaterService;

    @PostMapping("/{theaterId}")
    public ResponseEntity<SuccessResponse<FavoriteTheaterResponse>> createFavoriteTheater(
            @PathVariable Long userId,
            @PathVariable Long theaterId
    ) {
        FavoriteTheaterResponse response = favoriteTheaterService.createFavoriteTheater(userId, theaterId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }

    @DeleteMapping("/{theaterId}")
    public ResponseEntity<SuccessResponse<Void>> deleteFavoriteTheater(
            @PathVariable Long userId,
            @PathVariable Long theaterId
    ) {
        favoriteTheaterService.deleteFavoriteTheater(userId, theaterId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", null));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<FavoriteTheaterResponse>>> getFavoriteTheaters(
            @PathVariable Long userId
    ) {
        List<FavoriteTheaterResponse> response = favoriteTheaterService.getFavoriteTheaters(userId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }
}