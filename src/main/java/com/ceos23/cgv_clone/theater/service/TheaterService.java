package com.ceos23.cgv_clone.theater.service;

import com.ceos23.cgv_clone.common.ApiResponse;
import com.ceos23.cgv_clone.common.codes.ErrorCode;
import com.ceos23.cgv_clone.common.codes.SuccessCode;
import com.ceos23.cgv_clone.config.exception.CustomException;
import com.ceos23.cgv_clone.theater.domain.Theater;
import com.ceos23.cgv_clone.theater.dto.response.TheaterResponse;
import com.ceos23.cgv_clone.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheaterService {

    private final TheaterRepository theaterRepository;

    // 영화관 상세 조회
    @Transactional(readOnly = true)
    public ApiResponse<TheaterResponse> getTheater(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.THEATER_NOT_FOUND));

        // 상세 조회 성공 시
        return ApiResponse.ok(SuccessCode.SELECT_SUCCESS, TheaterResponse.from(theater));
    }

    // 지역별 영화관 조회
    @Transactional(readOnly = true)
    public ApiResponse<List<TheaterResponse>> getTheatersByRegion(String region) {
        List<Theater> theaters;

        // region 값이 없을 경우에는 전체 영화관 반환
        if (region == null) {
            theaters = theaterRepository.findAll();
        } else {
            theaters = theaterRepository.findAllByRegion(region);

            // 만약 반환값이 빈 값일 경우에는 Exception 반환
            if (theaters.isEmpty()) {
                throw new CustomException(ErrorCode.THEATER_NOT_FOUND);
            }
        }

        List<TheaterResponse> response = theaters.stream()
                .map(TheaterResponse::from)
                .toList();

        // 지역별 조회 성공 시
        return ApiResponse.ok(SuccessCode.SELECT_SUCCESS, response);
    }
}
