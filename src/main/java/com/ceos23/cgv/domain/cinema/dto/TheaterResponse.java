package com.ceos23.cgv.domain.cinema.dto;

import com.ceos23.cgv.domain.cinema.entity.Theater;
import com.ceos23.cgv.domain.cinema.enums.TheaterType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TheaterResponse {
    private Long id;
    private String name;
    private TheaterType type;
    private String maxRow;
    private int maxCol;

    public static TheaterResponse from(Theater theater) {
        return TheaterResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .type(theater.getType())
                .maxRow(theater.getMaxRow())
                .maxCol(theater.getMaxCol())
                .build();
    }
}