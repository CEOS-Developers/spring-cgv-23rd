package com.ceos.spring_cgv_23rd.domain.theater.application.service;

import com.ceos.spring_cgv_23rd.domain.theater.dto.TheaterResponseDTO;

import java.util.List;

public interface TheaterService {

    List<TheaterResponseDTO.TheaterListResponseDTO> getTheaterList();

    TheaterResponseDTO.TheaterDetailResponseDTO getTheaterDetail(Long theaterId);

    TheaterResponseDTO.TheaterLikeResponseDTO toggleTheaterLike(Long userId, Long theaterId);
}
