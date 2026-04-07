package com.ceos.spring_cgv_23rd.domain.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class MovieLike {

    private final Long id;
    private final Long userId;
    private final Long movieId;

    public static MovieLike create(Long userId, Long movieId) {
        return MovieLike.builder()
                .userId(userId)
                .movieId(movieId)
                .build();
    }
}
