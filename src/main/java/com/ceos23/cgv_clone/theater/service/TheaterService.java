package com.ceos23.cgv_clone.theater.service;

import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.theater.entity.Theater;
import com.ceos23.cgv_clone.theater.dto.response.TheaterResponse;
import com.ceos23.cgv_clone.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheaterService {

    private final TheaterRepository theaterRepository;

	@Cacheable(value = "theaterDetail", key = "#theaterId")
    @Transactional(readOnly = true)
    public TheaterResponse getTheater(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.THEATER_NOT_FOUND));

        return TheaterResponse.from(theater);
    }

	@Cacheable(value = "theatersByRegion", key = "#region == null ? 'ALL' : #region")
    @Transactional(readOnly = true)
    public List<TheaterResponse> getTheatersByRegion(String region) {
        if (!StringUtils.hasText(region)) {
            return theaterRepository.findAll().stream()
                    .map(TheaterResponse::from)
                    .toList();
        }

        List<Theater> theaters = theaterRepository.findAllByRegion(region);

        if (theaters.isEmpty()) {
            throw new CustomException(ErrorCode.THEATER_NOT_FOUND);
        }

        return theaters.stream()
                .map(TheaterResponse::from)
                .toList();
    }
}
