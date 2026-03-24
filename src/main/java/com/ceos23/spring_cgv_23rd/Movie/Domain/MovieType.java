package com.ceos23.spring_cgv_23rd.Movie.Domain;

import lombok.Getter;

@Getter
public enum MovieType {
    ACTION("액션"),
    THRILLER("스릴러"),
    COMEDY("코미디"),
    ROMANCE("로맨스"),
    DRAMA("드라마"),
    FANTASY("판타지"),
    SF("SF"),
    ANIMATION("애니메이션"),
    DOCUMENTARY("다큐멘터리"),
    FAMILY("가족"),
    HISTORY("역사"),
    WAR("전쟁");

    private String type;

    MovieType(String type) {
        this.type = type;
    }

}
