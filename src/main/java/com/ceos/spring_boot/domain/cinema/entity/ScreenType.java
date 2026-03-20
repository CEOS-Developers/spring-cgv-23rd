package com.ceos.spring_boot.domain.cinema.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScreenType {
    GENERAL("일반관"),
    SPECIAL("특별관");

    private final String description;
}
