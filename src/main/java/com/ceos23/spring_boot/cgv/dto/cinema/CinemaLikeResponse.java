package com.ceos23.spring_boot.cgv.dto.cinema;

import com.ceos23.spring_boot.cgv.domain.like.CinemaLike;
import java.time.LocalDateTime;

public record CinemaLikeResponse(
        Long cinemaId,
        String name,
        String address,
        LocalDateTime likedAt
) {
    public static CinemaLikeResponse from(CinemaLike cinemaLike) {
        return new CinemaLikeResponse(
                cinemaLike.getCinema().getId(),
                cinemaLike.getCinema().getName(),
                cinemaLike.getCinema().getAddress(),
                cinemaLike.getCreatedAt()
        );
    }
}
