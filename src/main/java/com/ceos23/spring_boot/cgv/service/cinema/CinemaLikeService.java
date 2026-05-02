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
import java.util.List;
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
            throw new ConflictException(ErrorCode.ALREADY_LIKED_CINEMA);
        }

        User user = findUserById(userId);
        Cinema cinema = findCinemaById(cinemaId);

        CinemaLike cinemaLike = new CinemaLike(user, cinema);
        cinemaLikeRepository.save(cinemaLike);
    }

    public void unlikeCinema(Long userId, Long cinemaId) {
        CinemaLike cinemaLike = cinemaLikeRepository.findByUserIdAndCinemaId(userId, cinemaId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CINEMA_LIKE_NOT_FOUND));

        cinemaLikeRepository.delete(cinemaLike);
    }

    @Transactional(readOnly = true)
    public List<CinemaLike> getLikedCinemas(Long userId) {
        findUserById(userId);
        return cinemaLikeRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private Cinema findCinemaById(Long cinemaId) {
        return cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CINEMA_NOT_FOUND));
    }
}
