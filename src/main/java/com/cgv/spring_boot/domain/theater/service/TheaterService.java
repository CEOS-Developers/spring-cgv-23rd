package com.cgv.spring_boot.domain.theater.service;

import com.cgv.spring_boot.domain.theater.dto.TheaterResponse;
import com.cgv.spring_boot.domain.theater.entity.Theater;
import com.cgv.spring_boot.domain.theater.repository.TheaterRepository;
import com.cgv.spring_boot.global.common.code.ErrorCode;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TheaterService {

    private final TheaterRepository theaterRepository;

    public List<TheaterResponse> findAllTheaters() {
        return theaterRepository.findAll().stream()
                .map(TheaterResponse::from)
                .toList();
    }

    public TheaterResponse findTheaterById(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.THEATER_NOT_FOUND));
        return TheaterResponse.from(theater);
    }
}
