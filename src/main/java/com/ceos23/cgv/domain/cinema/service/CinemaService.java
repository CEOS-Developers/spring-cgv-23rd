package com.ceos23.cgv.domain.cinema.service;

import com.ceos23.cgv.domain.cinema.dto.TheaterCreateRequest;
import com.ceos23.cgv.domain.cinema.entity.Cinema;
import com.ceos23.cgv.domain.cinema.entity.Theater;
import com.ceos23.cgv.domain.cinema.enums.Region;
import com.ceos23.cgv.domain.cinema.repository.CinemaRepository;
import com.ceos23.cgv.domain.cinema.repository.TheaterRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final TheaterRepository theaterRepository;

    /**
     * 전체 영화관(지점) 목록 조회
     */
    public List<Cinema> getAllCinema() {
        return cinemaRepository.findAll();
    }

    /**
     * 단일 영화관(지점) 상세 조회
     */
    public Cinema getCinemaDetails(Long cinemaId) {
        return cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new CustomException(ErrorCode.CINEMA_NOT_FOUND));
    }

    /**
     * 특정 영화관의 전체 상영관(Theater) 목록 조회
     */
    public List<Theater> getTheatersByCinemaId(Long cinemaId) {
        // 먼저 영화관이 존재하는지 검증
        if (!cinemaRepository.existsById(cinemaId)) {
            throw new CustomException(ErrorCode.CINEMA_NOT_FOUND);
        }
        return theaterRepository.findByCinemaId(cinemaId);
    }

    /**
     * [POST] 새로운 영화관(지점) 생성
     */
    @Transactional
    public Cinema createCinema(String name, Region region) {
        Cinema cinema = Cinema.create(name, region);
        return cinemaRepository.save(cinema);
    }

    /**
     * [POST] 특정 영화관 내에 새로운 상영관 생성
     */
    @Transactional
    public Theater createTheater(Long cinemaId, TheaterCreateRequest request) {
        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new CustomException(ErrorCode.CINEMA_NOT_FOUND));

        Theater theater = Theater.create(
                cinema,
                request.name(),
                request.type(),
                request.maxRow(),
                request.maxCol()
        );

        return theaterRepository.save(theater);
    }
}
