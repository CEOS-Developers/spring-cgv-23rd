package com.ceos23.cgv.domain.person.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PersonType {
    ACTOR("배우"),
    DIRECTOR("감독"),
    WRITER("작가"),
    PRODUCER("제작자");

    private final String description;
}
