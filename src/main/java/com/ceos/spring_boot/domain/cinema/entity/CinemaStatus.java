package com.ceos.spring_boot.domain.cinema.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CinemaStatus {
    OPERATING("운영중"),
    PREPARING("영업전"),
    CLOSED("폐점"),
    RENOVATING("리모델링중");

    private final String description;
}
