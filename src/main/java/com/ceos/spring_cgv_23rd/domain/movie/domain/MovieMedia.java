package com.ceos.spring_cgv_23rd.domain.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MovieMedia {

    private final Long id;
    private final Long movieId;
    private final MediaType mediaType;
    private final String mediaUrl;
}
