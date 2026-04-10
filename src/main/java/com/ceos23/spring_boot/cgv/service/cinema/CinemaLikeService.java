package com.ceos23.spring_boot.cgv.service.cinema;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.domain.like.CinemaLike;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.cinema.CinemaRepository;
import com.ceos23.spring_boot.cgv.repository.like.CinemaLikeRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CinemaLikeService {

    private final CinemaLikeRepository cinemaLikeRepository;
    private final CinemaRepository cinemaRepository;
    private final UserRepository userRepository;

    public CinemaLikeService(
            CinemaLikeRepository cinemaLikeRepository,
            CinemaRepository cinemaRepository,
            UserRepository userRepository
    ) {
        this.cinemaLikeRepository = cinemaLikeRepository;
        this.cinemaRepository = cinemaRepository;
        this.userRepository = userRepository;
    }

    public void likeCinema(Long userId, Long cinemaId) {
        if (cinemaLikeRepository.existsByUserIdAndCinemaId(userId, cinemaId)) {
            throw new ConflictException(ErrorCode.CONFLICT);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CINEMA_NOT_FOUND));

        CinemaLike cinemaLike = new CinemaLike(user, cinema);
        cinemaLikeRepository.save(cinemaLike);
    }

    public void unlikeCinema(Long userId, Long cinemaId) {
        CinemaLike cinemaLike = cinemaLikeRepository.findByUserIdAndCinemaId(userId, cinemaId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));

        cinemaLikeRepository.delete(cinemaLike);
    }
}