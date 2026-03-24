package com.ceos.spring_cgv_23rd.domain.screening.service;

import com.ceos.spring_cgv_23rd.domain.screening.dto.ScreeningResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ScreeningService {

    List<ScreeningResponseDTO.ScreeningByMovieResponseDTO> getScreeningByMovie(Long movieId, Long theaterId, LocalDate date);

    List<ScreeningResponseDTO.ScreeningByTheaterResponseDTO> getScreeningByTheater(Long theaterId, LocalDate date);
}
