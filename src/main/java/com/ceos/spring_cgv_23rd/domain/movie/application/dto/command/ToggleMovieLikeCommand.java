package com.ceos.spring_cgv_23rd.domain.movie.application.dto.command;

public record ToggleMovieLikeCommand(Long userId, Long movieId) {

}
