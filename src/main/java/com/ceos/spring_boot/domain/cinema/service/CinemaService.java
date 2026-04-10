package com.ceos.spring_boot.domain.cinema.service;

import com.ceos.spring_boot.domain.cinema.dto.CinemaCreateRequest;
import com.ceos.spring_boot.domain.cinema.dto.CinemaListResponse;
import com.ceos.spring_boot.domain.cinema.dto.CinemaResponse;
import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.cinema.repository.CinemaRepository;
import com.ceos.spring_boot.global.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CinemaService {
    private final CinemaRepository cinemaRepository;

    // 영화관 생성
    @Transactional
    public CinemaResponse createCinema(CinemaCreateRequest request) {
        Cinema cinema = Cinema.builder()
                .name(request.name())
                .region(request.region())
                .address(request.address())
                .status(request.status())
                .build();

        Cinema savedCinema = cinemaRepository.save(cinema);
        return CinemaResponse.from(savedCinema);
    }

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
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CINEMA_NOT_FOUND_ERROR.getMessage()));
        return CinemaResponse.from(cinema);
    }

    // 영화관 삭제
    @Transactional
    public void deleteCinema(Long id) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CINEMA_NOT_FOUND_ERROR.getMessage()));

        cinemaRepository.delete(cinema);
    }
}