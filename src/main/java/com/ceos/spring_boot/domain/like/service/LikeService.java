package com.ceos.spring_boot.domain.like.service;

import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.cinema.repository.CinemaRepository;
import com.ceos.spring_boot.domain.like.dto.CinemaLikeResponse;
import com.ceos.spring_boot.domain.like.dto.MovieLikeResponse;
import com.ceos.spring_boot.domain.like.entity.CinemaLike;
import com.ceos.spring_boot.domain.like.entity.MovieLike;
import com.ceos.spring_boot.domain.like.repository.CinemaLikeRepository;
import com.ceos.spring_boot.domain.like.repository.MovieLikeRepository;
import com.ceos.spring_boot.domain.movie.entity.Movie;
import com.ceos.spring_boot.domain.movie.repository.MovieRepository;
import com.ceos.spring_boot.domain.user.entity.User;
import com.ceos.spring_boot.domain.user.repository.UserRepository;
import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final MovieLikeRepository movieLikeRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CinemaLikeRepository cinemaLikeRepository;
    private final CinemaRepository cinemaRepository;

    public MovieLikeResponse toggleMovieLike(Long userId, Long movieId) {

        // 이미 찜 했는지 조회
        Optional<MovieLike> movieLike = movieLikeRepository.findByUserIdAndMovieId(userId, movieId);

        // 이미 찜 상태면 찜 취소(토글 방법)
        if (movieLike.isPresent()) {
            movieLikeRepository.delete(movieLike.get());
            return MovieLikeResponse.of(userId, movieId, false);
        }

        // 찜 기능 성능 최적화: getReferenceById 활용
        // getReferenceById는 실제 DB 조회를 하지 않고 프록시 객체만 생성하여 연관관계를 맺음
        User user = userRepository.getReferenceById(userId);

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND_ERROR));

        movieLikeRepository.save(MovieLike.create(user, movie));

        return MovieLikeResponse.of(userId, movieId, true);
    }


    public CinemaLikeResponse toggleCinemaLike(Long userId, Long cinemaId) {

        Optional<CinemaLike> cinemaLike = cinemaLikeRepository.findByUserIdAndCinemaId(userId, cinemaId);

        if (cinemaLike.isPresent()) {
            cinemaLikeRepository.delete(cinemaLike.get());
            return CinemaLikeResponse.of(userId, cinemaId, false);
        }

        User user = userRepository.getReferenceById(userId);

        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CINEMA_NOT_FOUND_ERROR));

        cinemaLikeRepository.save(CinemaLike.create(user, cinema));

        return CinemaLikeResponse.of(userId, cinemaId, true);
    }
}