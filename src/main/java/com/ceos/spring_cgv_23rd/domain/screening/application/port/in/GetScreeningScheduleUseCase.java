package com.ceos.spring_cgv_23rd.domain.screening.application.port.in;

import com.ceos.spring_cgv_23rd.domain.screening.application.dto.result.ScreeningDetailResult;

import java.time.LocalDate;
import java.util.List;

public interface GetScreeningScheduleUseCase {

    List<ScreeningDetailResult> getScreeningByMovie(Long movieId, Long theaterId, LocalDate date);

    List<ScreeningDetailResult> getScreeningByTheater(Long theaterId, LocalDate date);
}
