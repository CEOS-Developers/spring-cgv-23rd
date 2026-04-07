package com.ceos.spring_cgv_23rd.domain.theater.application.port.in;

import com.ceos.spring_cgv_23rd.domain.theater.application.dto.command.ToggleTheaterLikeCommand;
import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.ToggleTheaterLikeResult;

public interface ToggleTheaterLikeUseCase {

    ToggleTheaterLikeResult execute(ToggleTheaterLikeCommand command);
}
