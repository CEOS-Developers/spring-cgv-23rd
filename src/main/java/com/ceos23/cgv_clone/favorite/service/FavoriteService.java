package com.ceos23.cgv_clone.favorite.service;

import com.ceos23.cgv_clone.common.ApiResponse;
import com.ceos23.cgv_clone.common.codes.ErrorCode;
import com.ceos23.cgv_clone.common.codes.SuccessCode;
import com.ceos23.cgv_clone.config.exception.CustomException;
import com.ceos23.cgv_clone.favorite.domain.TheaterFavorite;
import com.ceos23.cgv_clone.favorite.repository.TheaterFavoriteRepository;
import com.ceos23.cgv_clone.theater.domain.Theater;
import com.ceos23.cgv_clone.theater.repository.TheaterRepository;
import com.ceos23.cgv_clone.user.domain.User;
import com.ceos23.cgv_clone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    final private UserRepository userRepository;
    final private TheaterRepository theaterRepository;
    final private TheaterFavoriteRepository theaterFavoriteRepository;

    // 1. 영화관 찜
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
