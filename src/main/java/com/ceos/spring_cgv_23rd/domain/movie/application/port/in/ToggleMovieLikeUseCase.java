package com.ceos.spring_cgv_23rd.domain.movie.application.port.in;

import com.ceos.spring_cgv_23rd.domain.movie.application.dto.command.ToggleMovieLikeCommand;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.ToggleMovieLikeResult;

public interface ToggleMovieLikeUseCase {

    ToggleMovieLikeResult execute(ToggleMovieLikeCommand command);
}
