package com.ceos.spring_cgv_23rd.domain.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class Contributor {

    private final Long id;
    private final String name;
    private final String profileImageUrl;
}
