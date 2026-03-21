package com.ceos23.spring_boot.cgv.service.cinema;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.cinema.CinemaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CinemaService {

    private final CinemaRepository cinemaRepository;

    public List<Cinema> getCinemas() {
        return cinemaRepository.findAll();
    }

    public Cinema getCinema(Long cinemaId) {
        return cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.CINEMA_NOT_FOUND,
                        "해당 영화관이 존재하지 않습니다. id=" + cinemaId
                ));
    }
}