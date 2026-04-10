package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.dto.FavoriteMovieResponse;
import com.ceos23.spring_boot.global.response.SuccessResponse;
import com.ceos23.spring_boot.service.FavoriteMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/favorite-movies")
public class FavoriteMovieController {

    private final FavoriteMovieService favoriteMovieService;

    @PostMapping("/{movieId}")
    public ResponseEntity<SuccessResponse<FavoriteMovieResponse>> createFavoriteMovie(
            @PathVariable Long userId,
            @PathVariable Long movieId
    ) {
        FavoriteMovieResponse response = favoriteMovieService.createFavoriteMovie(userId, movieId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<SuccessResponse<Void>> deleteFavoriteMovie(
            @PathVariable Long userId,
            @PathVariable Long movieId
    ) {
        favoriteMovieService.deleteFavoriteMovie(userId, movieId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", null));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<FavoriteMovieResponse>>> getFavoriteMovies(
            @PathVariable Long userId
    ) {
        List<FavoriteMovieResponse> response = favoriteMovieService.getFavoriteMovies(userId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }
}