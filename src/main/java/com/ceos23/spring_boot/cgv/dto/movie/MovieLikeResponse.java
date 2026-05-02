package com.ceos23.spring_boot.cgv.dto.movie;

import com.ceos23.spring_boot.cgv.domain.like.MovieLike;
import java.time.LocalDateTime;

public record MovieLikeResponse(
        Long movieId,
        String title,
        Integer runningTime,
        String rating,
        String description,
        LocalDateTime likedAt
) {
    public static MovieLikeResponse from(MovieLike movieLike) {
        return new MovieLikeResponse(
                movieLike.getMovie().getId(),
                movieLike.getMovie().getTitle(),
                movieLike.getMovie().getRunningTime(),
                movieLike.getMovie().getRating(),
                movieLike.getMovie().getDescription(),
                movieLike.getLikedAt()
        );
    }
}
