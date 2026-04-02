package com.ceos23.spring_boot.domain.user.service;

import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.domain.theater.repository.TheaterRepository;
import com.ceos23.spring_boot.domain.user.dto.FavoriteTheaterInfo;
import com.ceos23.spring_boot.domain.user.entity.FavoriteTheater;
import com.ceos23.spring_boot.domain.user.entity.User;
import com.ceos23.spring_boot.domain.user.repository.FavoriteTheaterRepository;
import com.ceos23.spring_boot.domain.user.repository.UserRepository;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
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

    public List<FavoriteTheaterInfo> findFavoriteTheaters(String email) {
        if (!userRepository.existsByEmailAndDeletedAtIsNull(email)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        List<FavoriteTheater> favoriteTheaters = favoriteTheaterRepository.findAllByUserEmail(email);

        return favoriteTheaters.stream()
                .map(FavoriteTheaterInfo::from)
                .toList();
    }

    @Transactional
    public boolean toggleFavorite(String email, Long theaterId) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Theater theater = theaterRepository.findByIdAndDeletedAtIsNull(theaterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.THEATER_NOT_FOUND));

        return favoriteTheaterRepository.findByUserAndTheater(user, theater)
                .map(ft -> {
                    favoriteTheaterRepository.delete(ft);
                    return false;
                })
                .orElseGet(() -> {
                    favoriteTheaterRepository.save(
                            FavoriteTheater.builder()
                                    .user(user)
                                    .theater(theater)
                                    .build());
                    return true;
                });
    }
}
