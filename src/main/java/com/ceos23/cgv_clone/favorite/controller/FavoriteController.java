package com.ceos23.cgv_clone.favorite.controller;

import com.ceos23.cgv_clone.common.ApiResponse;
import com.ceos23.cgv_clone.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/theaters/{theaterId}")
    public ApiResponse<Void> toggleFavoriteTheater(
            @RequestHeader Long userId,
            @PathVariable Long theaterId
    ) {
        return favoriteService.toggleFavoriteTheater(userId, theaterId);
    }

    @PostMapping("/movie/{movieId}")
    public ApiResponse<Void> toggleFavoriteMovie(
            @RequestHeader Long userId,
            @PathVariable Long movieId
    ) {
        return favoriteService.toggleFavoriteMovie(userId, movieId);
    }
}
