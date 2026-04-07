package com.ceos.spring_cgv_23rd.domain.theater.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Seat {

    private final Long id;
    private final HallType hallType;
    private final Integer rowNum;
    private final Integer colNum;
    private final Boolean isUsable;
}
