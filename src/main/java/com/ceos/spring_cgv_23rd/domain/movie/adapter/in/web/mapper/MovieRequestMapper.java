package com.ceos.spring_cgv_23rd.domain.movie.adapter.in.web.mapper;

import com.ceos.spring_cgv_23rd.domain.movie.application.dto.command.ToggleMovieLikeCommand;
import org.springframework.stereotype.Component;

@Component
public class MovieRequestMapper {

    public ToggleMovieLikeCommand toToggleLikeCommand(Long userId, Long movieId) {
        return new ToggleMovieLikeCommand(userId, movieId);
    }
}
