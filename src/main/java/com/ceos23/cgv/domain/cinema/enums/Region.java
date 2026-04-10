package com.ceos23.cgv.domain.cinema.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Region {
    SEOUL("서울"),
    GYEONGGI("경기"),
    INCHEON("인천"),
    GANGWON("강원"),
    DAEJEON("대전"),
    CHUNGCHEONG("충청"),
    DAEGU("대구"),
    BUSAN("부산"),
    ULSAN("울산"),
    GYEONGSANG("경상"),
    GWANGJU("광주"),
    JEOLLA("전라"),
    JEJU("제주");

    private final String description;

    @JsonCreator
    public static Region from(String value) {
        for (Region region : Region.values()) {
            if (region.getDescription().equals(value) || region.name().equals(value)) {
                return region;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 지역 이름입니다: " + value);
    }
}