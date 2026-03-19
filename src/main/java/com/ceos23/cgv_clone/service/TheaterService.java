package com.ceos23.cgv_clone.service;

import com.ceos23.cgv_clone.common.ApiResponse;
import com.ceos23.cgv_clone.common.codes.ErrorCode;
import com.ceos23.cgv_clone.common.codes.SuccessCode;
import com.ceos23.cgv_clone.config.exception.CustomException;
import com.ceos23.cgv_clone.domain.favorite.TheaterFavorite;
import com.ceos23.cgv_clone.domain.theater.Theater;
import com.ceos23.cgv_clone.domain.user.User;
import com.ceos23.cgv_clone.dto.theater.response.TheaterResponse;
import com.ceos23.cgv_clone.repository.TheaterFavoriteRepository;
import com.ceos23.cgv_clone.repository.TheaterRepository;
import com.ceos23.cgv_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheaterService {

    private final TheaterRepository theaterRepository;
    private final UserRepository userRepository;
    private final TheaterFavoriteRepository theaterFavoriteRepository;

    // 1. 영화관 상세 조회
    @Transactional(readOnly = true)
    public ApiResponse<TheaterResponse> getTheater(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.THEATER_NOT_FOUND));

        // 상세 조회 성공 시
        return ApiResponse.ok(SuccessCode.SELECT_SUCCESS, TheaterResponse.from(theater));
    }

    // 2. 지역별 영화관 조회
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

    // 3. 영화관 찜
    @Transactional
    public ApiResponse<Void> toggleFavoriteTheater(Long userId, Long theaterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.THEATER_NOT_FOUND));

        // 자주가는 영화관 5개 초과일 경우
        if (theaterFavoriteRepository.countByUser(user) >= 5) {
            throw new CustomException(ErrorCode.FAVORITE_THEATER_LIMIT_EXCEEDED);
        }

        TheaterFavorite favorite = TheaterFavorite.builder()
                .user(user)
                .theater(theater)
                .build();

        // 이미 자주가는 곳으로 되어 있을 경우에는 삭제
        if (theaterFavoriteRepository.existsByUserAndTheater(user, theater)) {
            theaterFavoriteRepository.delete(favorite);
            return ApiResponse.ok(SuccessCode.DELETE_SUCCESS);
        } else {
            // 자주가는 곳이 아닐 경우에는 저장
            theaterFavoriteRepository.save(favorite);
            return ApiResponse.ok(SuccessCode.INSERT_SUCCESS);
        }
    }

}
