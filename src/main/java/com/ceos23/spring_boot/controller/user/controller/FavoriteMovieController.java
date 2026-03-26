package com.ceos23.spring_boot.controller.user.controller;

import com.ceos23.spring_boot.controller.user.dto.FavoriteMovieDto.FavoriteMovieResponse;
import com.ceos23.spring_boot.controller.user.dto.FavoriteMovieDto.FavoriteMovieSearchRequest;
import com.ceos23.spring_boot.controller.user.dto.FavoriteMovieDto.FavoriteMovieToggleResponse;
import com.ceos23.spring_boot.domain.user.service.FavoriteMovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "5. FavoriteMovie (영화 찜)", description = "영화 찜 API")
@RestController
@RequiredArgsConstructor
public class FavoriteMovieController {
    private final FavoriteMovieService favoriteMovieService;

    @Operation(summary = "찜한 영화 목록 조회", description = "찜한 전체 영화 목록을 조회합니다.")
    @GetMapping("/api/favoriteMovies")
    public ResponseEntity<List<FavoriteMovieResponse>> findFavoriteMovies(
            @ParameterObject
            @Valid @ModelAttribute FavoriteMovieSearchRequest request
    ) {
        List<FavoriteMovieResponse> responses = favoriteMovieService.findFavoriteMovies(request.toCommand())
                .stream()
                .map(FavoriteMovieResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "영화 찜 토클 (찜하기/취소하기)")
    @PostMapping("/{movieId}/favorites")
    public ResponseEntity<FavoriteMovieToggleResponse> toggleFavorite(
            //변경 필수!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long movieId
    ) {
        boolean isFavorited = favoriteMovieService.toggleFavorite(userId, movieId);

        return ResponseEntity.ok(FavoriteMovieToggleResponse.from(isFavorited));
    }
}
