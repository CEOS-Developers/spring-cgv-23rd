package com.ceos.spring_boot.domain.movie.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeRating {
    ALL("전체관람가"),
    AGE_12("12세이상관람가"),
    AGE_15("15세이상관람가"),
    ADULT("청소년관람불가");

    private final String description;
}