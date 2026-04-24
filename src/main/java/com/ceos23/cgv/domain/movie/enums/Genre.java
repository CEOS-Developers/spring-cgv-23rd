package com.ceos23.cgv.domain.movie.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Genre {
    ACTION("액션"),
    COMEDY("코미디"),
    ROMANCE("로맨스"),
    THRILLER("스릴러"),
    HORROR("공포"),
    SF("SF"),
    ANIMATION("애니메이션"),
    DRAMA("드라마"),
    FANTASY("판타지");

    private final String description;
}
