package com.ceos23.cgv.domain.cinema.controller;

import com.ceos23.cgv.domain.cinema.dto.ReviewCreateRequest;
import com.ceos23.cgv.domain.cinema.dto.ReviewResponse;
import com.ceos23.cgv.domain.cinema.entity.Review;
import com.ceos23.cgv.domain.cinema.service.ReviewService;
import com.ceos23.cgv.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review API", description = "영화 실관람평(리뷰) 작성 및 조회 API")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "관람평 작성", description = "특정 영화에 대한 관람평과 관람한 상영관 타입(IMAX 등)을 기록합니다.")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(@RequestBody ReviewCreateRequest request) {
        Review review = reviewService.createReview(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(ReviewResponse.from(review))); // created 메서드로 감싸서 반환
    }

    @GetMapping("/movie/{movieId}")
    @Operation(summary = "특정 영화의 관람평 조회", description = "영화 ID를 통해 해당 영화에 작성된 모든 관람평을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByMovie(@PathVariable Long movieId) {
        List<Review> reviews = reviewService.getReviewsByMovieId(movieId);

        return ResponseEntity.ok(ApiResponse.success(reviews, ReviewResponse::from));
    }
}