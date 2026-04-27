package com.ceos.spring_boot.domain.cinema.service;

import com.ceos.spring_boot.domain.cinema.dto.CinemaCreateRequest;
import com.ceos.spring_boot.domain.cinema.dto.CinemaListResponse;
import com.ceos.spring_boot.domain.cinema.dto.CinemaResponse;
import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.cinema.repository.CinemaRepository;
import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.exception.BusinessException;
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
        Cinema cinema = Cinema.create(
                request.name(),
                request.region(),
                request.address(),
                request.status()
        );

        return CinemaResponse.from(cinemaRepository.save(cinema));
    }

    // 모든 영화관 조회
    public CinemaListResponse findAllCinemas() {
        return CinemaListResponse.from(
                cinemaRepository.findAll().stream().map(CinemaResponse::from).toList()
        );
    }

    // 영화관 id로 영화관 조회
    public CinemaResponse findCinemaById(Long id) {
        return CinemaResponse.from(findEntityById(id));
    }

    // 영화관 삭제
    @Transactional
    public void deleteCinema(Long id) {
        cinemaRepository.delete(findEntityById(id));
    }

    // 내부에서 공통으로 사용하는 엔티티 조회 로직 분리 (-> 중복 제거)
    private Cinema findEntityById(Long id) {
        return cinemaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CINEMA_NOT_FOUND_ERROR));
    }
}