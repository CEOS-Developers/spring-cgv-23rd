package com.ceos23.cgv_clone.favorite.controller;

import com.ceos23.cgv_clone.global.jwt.CustomUserDetails;
import com.ceos23.cgv_clone.global.response.ApiResponse;
import com.ceos23.cgv_clone.global.response.SuccessCode;
import com.ceos23.cgv_clone.favorite.dto.response.FavoriteResponse;
import com.ceos23.cgv_clone.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/theaters/{theaterId}")
    public ApiResponse<FavoriteResponse> toggleFavoriteTheater(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long theaterId
    ) {
        return ApiResponse.ok(SuccessCode.INSERT_SUCCESS, favoriteService.toggleFavoriteTheater(userDetails.getUserId(), theaterId));
    }

    @PostMapping("/movies/{movieId}")
    public ApiResponse<FavoriteResponse> toggleFavoriteMovie(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long movieId
    ) {
        return ApiResponse.ok(SuccessCode.INSERT_SUCCESS, favoriteService.toggleFavoriteMovie(userDetails.getUserId(), movieId));
    }

}
