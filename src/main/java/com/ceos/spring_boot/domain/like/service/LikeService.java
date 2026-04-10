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

        // 사용자 및 영화 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND_ERROR.getMessage()));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.MOVIE_NOT_FOUND_ERROR.getMessage()));

        // 이미 찜했는지 조회
        Optional<MovieLike> movieLike = movieLikeRepository.findByUserIdAndMovieId(userId, movieId);

        boolean isLiked;

        if (movieLike.isPresent()) {
            movieLikeRepository.delete(movieLike.get());
            isLiked = false; // 삭제되었으므로 false
        } else {
            MovieLike newLike = MovieLike.builder()
                    .user(user)
                    .movie(movie)
                    .build();
            movieLikeRepository.save(newLike);
            isLiked = true; // 저장되었으므로 true
        }
        return MovieLikeResponse.of(userId,movieId,isLiked);
    }


    public CinemaLikeResponse toggleCinemaLike(Long userId, Long cinemaId) {

        // 사용자 및 영화관 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND_ERROR.getMessage()));

        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CINEMA_NOT_FOUND_ERROR.getMessage()));

        // 이미 찜했는지 조회
        Optional<CinemaLike> cinemaLike = cinemaLikeRepository.findByUserIdAndCinemaId(userId, cinemaId);

        boolean isLiked;

        if (cinemaLike.isPresent()) {
            cinemaLikeRepository.delete(cinemaLike.get());
            isLiked = false; // 삭제되었으므로 false
        } else {
            CinemaLike newLike = CinemaLike.builder()
                    .user(user)
                    .cinema(cinema)
                    .build();
            cinemaLikeRepository.save(newLike);
            isLiked = true; // 저장되었으므로 true
        }
        return CinemaLikeResponse.of(userId,cinemaId,isLiked);
    }
}