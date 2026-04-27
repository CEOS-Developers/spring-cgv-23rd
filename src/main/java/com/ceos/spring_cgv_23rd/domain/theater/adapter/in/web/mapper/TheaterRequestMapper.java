package com.ceos.spring_cgv_23rd.domain.theater.adapter.in.web.mapper;

import com.ceos.spring_cgv_23rd.domain.theater.application.dto.command.ToggleTheaterLikeCommand;
import org.springframework.stereotype.Component;

@Component
public class TheaterRequestMapper {

    public ToggleTheaterLikeCommand toggleTheaterLikeCommand(Long userId, Long theaterId) {
        return new ToggleTheaterLikeCommand(userId, theaterId);
    }
}
