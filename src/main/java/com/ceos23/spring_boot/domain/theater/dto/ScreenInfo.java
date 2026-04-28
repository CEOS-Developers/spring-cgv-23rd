package com.ceos23.spring_boot.domain.theater.dto;

import com.ceos23.spring_boot.domain.theater.entity.Screen;
import io.swagger.v3.oas.annotations.media.Schema;

public record ScreenInfo(
        Long id,
        Long theaterId,
        Long screenTypeId,
        String screenName
) {

    public static ScreenInfo from(Screen screen) {
        return new ScreenInfo(
                screen.getId(),
                screen.getTheater().getId(),
                screen.getScreenType().getId(),
                screen.getName()
        );
    }
}
