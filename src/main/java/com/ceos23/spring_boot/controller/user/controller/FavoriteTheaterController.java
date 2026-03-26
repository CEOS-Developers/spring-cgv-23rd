package com.ceos23.spring_boot.controller.user.controller;

import com.ceos23.spring_boot.controller.user.dto.FavoriteTheaterDto.FavoriteTheaterResponse;
import com.ceos23.spring_boot.controller.user.dto.FavoriteTheaterDto.FavoriteTheaterSearchRequest;
import com.ceos23.spring_boot.controller.user.dto.FavoriteTheaterDto.FavoriteTheaterToggleResponse;
import com.ceos23.spring_boot.domain.user.service.FavoriteTheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "4. FavoriteTheater (영화관 찜)", description = "영화관 찜 API")
@RestController
@RequiredArgsConstructor
public class FavoriteTheaterController {
    private final FavoriteTheaterService favoriteTheaterService;

    @Operation(summary = "찜한 영화관 목록 조회", description = "찜한 전체 영화관 목록을 조회합니다.")
    @GetMapping("/api/favoriteTheaters")
    public ResponseEntity<List<FavoriteTheaterResponse>> findFavoriteTheaters(
            @ParameterObject
            @Valid @ModelAttribute FavoriteTheaterSearchRequest request
            ) {
        List<FavoriteTheaterResponse> responses = favoriteTheaterService.findFavoriteTheaters(request.toCommand())
                .stream()
                .map(FavoriteTheaterResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "영화관 찜 토클 (찜하기/취소하기)")
    @PostMapping("/{theaterId}/favorites")
    public ResponseEntity<FavoriteTheaterToggleResponse> toggleFavorite(
            //변경 필수!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long theaterId
    ) {
        boolean isFavorited = favoriteTheaterService.toggleFavorite(userId, theaterId);

        return ResponseEntity.ok(FavoriteTheaterToggleResponse.from(isFavorited));
    }
}
