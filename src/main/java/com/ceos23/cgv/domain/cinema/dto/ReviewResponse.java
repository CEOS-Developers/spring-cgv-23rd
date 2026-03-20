package com.ceos23.cgv.domain.cinema.dto;

import com.ceos23.cgv.domain.cinema.entity.Review;
import com.ceos23.cgv.domain.cinema.enums.TheaterType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
    private Long reviewId;
    private String authorName;
    private String movieTitle;
    private TheaterType theaterType;
    private String content;
    private int likeCount;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .authorName(review.getUser().getNickname())
                .movieTitle(review.getMovie().getTitle())
                .theaterType(review.getType())
                .content(review.getContent())
                .likeCount(review.getLikeCount())
                .createdAt(review.getCreatedAt())
                .build();
    }
}