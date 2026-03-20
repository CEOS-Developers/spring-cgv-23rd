package com.ceos.spring_boot.domain.cinema.service;

import com.ceos.spring_boot.domain.cinema.dto.CinemaListResponse;
import com.ceos.spring_boot.domain.cinema.dto.CinemaResponse;
import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.cinema.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CinemaService {
    private final CinemaRepository cinemaRepository;

    // 모든 영화관 조회
    public CinemaListResponse findAllCinemas() {
        List<CinemaResponse> cinemaResponses = cinemaRepository.findAll().stream()
                .map(CinemaResponse::from)
                .toList();
        return CinemaListResponse.from(cinemaResponses);
    }

    // 영화관 id로 영화관 조회
    public CinemaResponse findCinemaById(Long id) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 영화관을 찾을 수 없습니다." ));
        return CinemaResponse.from(cinema);
    }
}