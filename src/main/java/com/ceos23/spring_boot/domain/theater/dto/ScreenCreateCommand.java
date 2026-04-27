package com.ceos23.spring_boot.domain.theater.dto;

public record ScreenCreateCommand(
        Long theaterId,
        Long screenTypeId,
        String screenName
) {}