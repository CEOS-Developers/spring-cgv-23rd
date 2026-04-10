package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.FavoriteTheater;
import com.ceos23.spring_boot.domain.Theater;
import com.ceos23.spring_boot.domain.User;
import com.ceos23.spring_boot.dto.FavoriteTheaterResponse;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.repository.FavoriteTheaterRepository;
import com.ceos23.spring_boot.repository.TheaterRepository;
import com.ceos23.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteTheaterService {

    private final FavoriteTheaterRepository favoriteTheaterRepository;
    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;

    @Transactional
    public FavoriteTheaterResponse createFavoriteTheater(Long userId, Long theaterId) {
        validateId(userId);
        validateId(theaterId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.THEATER_NOT_FOUND));

        if (favoriteTheaterRepository.existsByUserIdAndTheaterId(userId, theaterId)) {
            throw new CustomException(ErrorCode.FAVORITE_THEATER_ALREADY_EXISTS);
        }

        FavoriteTheater favoriteTheater = FavoriteTheater.of(user, theater);
        FavoriteTheater savedFavoriteTheater = favoriteTheaterRepository.save(favoriteTheater);

        return FavoriteTheaterResponse.from(savedFavoriteTheater);
    }

    @Transactional
    public void deleteFavoriteTheater(Long userId, Long theaterId) {
        validateId(userId);
        validateId(theaterId);

        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        if (!theaterRepository.existsById(theaterId)) {
            throw new CustomException(ErrorCode.THEATER_NOT_FOUND);
        }

        if (!favoriteTheaterRepository.existsByUserIdAndTheaterId(userId, theaterId)) {
            throw new CustomException(ErrorCode.FAVORITE_THEATER_NOT_FOUND);
        }

        favoriteTheaterRepository.deleteByUserIdAndTheaterId(userId, theaterId);
    }

    public List<FavoriteTheaterResponse> getFavoriteTheaters(Long userId) {
        validateId(userId);

        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return favoriteTheaterRepository.findAllByUserId(userId).stream()
                .map(FavoriteTheaterResponse::from)
                .toList();
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id는 1 이상이어야 합니다.");
        }
    }
}