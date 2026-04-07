package com.ceos.spring_cgv_23rd.domain.theater.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HallType {

    private final Long id;
    private final String name;
    private final Integer totalRows;
    private final Integer totalCols;
}
