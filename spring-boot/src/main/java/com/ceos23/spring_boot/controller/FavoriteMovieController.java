package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.dto.FavoriteMovieResponse;
import com.ceos23.spring_boot.global.response.SuccessResponse;
import com.ceos23.spring_boot.service.FavoriteMovieService;
import com.ceos23.spring_boot.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite-movies")
public class FavoriteMovieController {

    private final FavoriteMovieService favoriteMovieService;

    @PostMapping("/{movieId}")
    public ResponseEntity<SuccessResponse<FavoriteMovieResponse>> createFavoriteMovie(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long movieId
    ) {
        Long userId = userDetails.getUserId();

        FavoriteMovieResponse response = favoriteMovieService.createFavoriteMovie(userId, movieId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<SuccessResponse<Void>> deleteFavoriteMovie(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long movieId
    ) {
        Long userId = userDetails.getUserId();

        favoriteMovieService.deleteFavoriteMovie(userId, movieId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", null));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<FavoriteMovieResponse>>> getFavoriteMovies(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        List<FavoriteMovieResponse> response = favoriteMovieService.getFavoriteMovies(userId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }
}