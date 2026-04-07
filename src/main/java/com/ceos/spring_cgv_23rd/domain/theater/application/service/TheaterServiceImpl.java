package com.ceos.spring_cgv_23rd.domain.theater.application.service;

import com.ceos.spring_cgv_23rd.domain.theater.dto.TheaterResponseDTO;
import com.ceos.spring_cgv_23rd.domain.theater.entity.Theater;
import com.ceos.spring_cgv_23rd.domain.theater.entity.TheaterLike;
import com.ceos.spring_cgv_23rd.domain.theater.exception.TheaterErrorCode;
import com.ceos.spring_cgv_23rd.domain.theater.repository.TheaterLikeRepository;
import com.ceos.spring_cgv_23rd.domain.theater.repository.TheaterRepository;
import com.ceos.spring_cgv_23rd.domain.user.entity.User;
import com.ceos.spring_cgv_23rd.domain.user.exception.UserErrorCode;
import com.ceos.spring_cgv_23rd.domain.user.repository.UserRepository;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheaterServiceImpl implements TheaterService {

    private final TheaterRepository theaterRepository;
    private final TheaterLikeRepository theaterLikeRepository;
    private final UserRepository userRepository;

    @Override
    public List<TheaterResponseDTO.TheaterListResponseDTO> getTheaterList() {

        // 전체 영화관 조회
        List<Theater> theaters = theaterRepository.findAll();

        return theaters.stream()
                .map(TheaterResponseDTO.TheaterListResponseDTO::from)
                .toList();
    }

    @Override
    public TheaterResponseDTO.TheaterDetailResponseDTO getTheaterDetail(Long theaterId) {

        // 영화관 조회
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND));

        return TheaterResponseDTO.TheaterDetailResponseDTO.from(theater);
    }

    @Override
    @Transactional
    public TheaterResponseDTO.TheaterLikeResponseDTO toggleTheaterLike(Long userId, Long theaterId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));


        // 영화관 조회
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND));

        // 영화관 찜 여부 조회
        Optional<TheaterLike> existingLike = theaterLikeRepository.findByUserIdAndTheaterId(userId, theaterId);

        boolean liked;
        if (existingLike.isPresent()) {
            // 찜 취소
            theaterLikeRepository.delete(existingLike.get());
            liked = false;
        } else {
            // 찜 등록
            theaterLikeRepository.save(TheaterLike.createTheaterLike(user, theater));
            liked = true;
        }

        return TheaterResponseDTO.TheaterLikeResponseDTO.of(theaterId, liked);
    }

}
