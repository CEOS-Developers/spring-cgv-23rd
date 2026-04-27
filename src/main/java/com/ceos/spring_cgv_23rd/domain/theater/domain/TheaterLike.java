package com.ceos.spring_cgv_23rd.domain.theater.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TheaterLike {
    
    private final Long id;
    private final Long userId;
    private final Long theaterId;

    public static TheaterLike create(Long userId, Long theaterId) {
        return TheaterLike.builder()
                .userId(userId)
                .theaterId(theaterId)
                .build();
    }
}
