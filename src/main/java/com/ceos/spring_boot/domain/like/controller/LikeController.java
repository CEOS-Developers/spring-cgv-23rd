package com.ceos.spring_boot.domain.like.controller;

import com.ceos.spring_boot.domain.like.dto.CinemaLikeResponse;
import com.ceos.spring_boot.domain.like.dto.MovieLikeResponse;
import com.ceos.spring_boot.domain.like.service.LikeService;
import com.ceos.spring_boot.global.security.CustomUserDetails;
import com.ceos.spring_boot.global.codes.SuccessCode;
import com.ceos.spring_boot.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Like 관련 API", description = "영화 및 영화관 찜하기 API입니다.")
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;

    // 영화 찜
    @PostMapping("/movies/{movieId}")
    @Operation(summary = "영화 찜", description = "원하는 영화를 찜 합니다.")
    public ResponseEntity<ApiResponse<MovieLikeResponse>> toggleMovieLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long movieId) {

        MovieLikeResponse response = likeService.toggleMovieLike(userDetails.getUser().getId(), movieId);

        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.UPDATE_SUCCESS));
    }

    // 영화관 찜
    @PostMapping("/cinemas/{cinemaId}")
    @Operation(summary = "영화관 찜", description = "원하는 영화관을 찜 합니다.")
    public ResponseEntity<ApiResponse<CinemaLikeResponse>> toggleCinemaLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cinemaId) {

        CinemaLikeResponse response = likeService.toggleCinemaLike(userDetails.getUser().getId(), cinemaId);

        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.UPDATE_SUCCESS));
    }

}
