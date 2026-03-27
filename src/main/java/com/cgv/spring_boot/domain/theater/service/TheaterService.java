package com.cgv.spring_boot.domain.theater.service;

import com.cgv.spring_boot.domain.theater.dto.TheaterResponse;
import com.cgv.spring_boot.domain.theater.entity.Theater;
import com.cgv.spring_boot.domain.theater.entity.TheaterWish;
import com.cgv.spring_boot.domain.theater.repository.TheaterRepository;
import com.cgv.spring_boot.domain.theater.repository.TheaterWishRepository;
import com.cgv.spring_boot.domain.user.entity.User;
import com.cgv.spring_boot.domain.user.repository.UserRepository;
import com.cgv.spring_boot.domain.theater.exception.TheaterErrorCode;
import com.cgv.spring_boot.domain.user.exception.UserErrorCode;
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
    private final TheaterWishRepository theaterWishRepository;
    private final UserRepository userRepository;

    public List<TheaterResponse> findAllTheaters() {
        return theaterRepository.findAll().stream()
                .map(TheaterResponse::from)
                .toList();
    }

    public TheaterResponse findTheaterById(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TheaterErrorCode.THEATER_NOT_FOUND));
        return TheaterResponse.from(theater);
    }

    /**
     * 로그인한 사용자가 영화관 찜을 생성
     * @param userId 사용자 ID
     * @param theaterId 영화관 ID
     * @return 영화관 찜 ID
     */
    @Transactional
    public Long wishTheater(Long userId, Long theaterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new BusinessException(TheaterErrorCode.THEATER_NOT_FOUND));

        if (theaterWishRepository.existsByUserIdAndTheaterId(userId, theaterId)) {
            throw new BusinessException(TheaterErrorCode.THEATER_ALREADY_WISHED);
        }

        TheaterWish theaterWish = TheaterWish.builder()
                .user(user)
                .theater(theater)
                .build();

        return theaterWishRepository.save(theaterWish).getId();
    }
}
