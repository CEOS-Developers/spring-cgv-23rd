package com.ceos.spring_cgv_23rd.domain.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class MovieCredit {

    private final Long id;
    private final Long movieId;
    private final Contributor contributor;
    private final RoleType roleType;
}
