package com.ceos23.cgv.domain.cinema.dto;

import com.ceos23.cgv.domain.cinema.enums.TheaterType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TheaterCreateRequest {
    private String name;
    private TheaterType type;
    private String maxRow;
    private int maxCol;
}