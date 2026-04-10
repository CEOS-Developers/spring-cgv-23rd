package com.ceos23.cgv_clone.favorite.service;

import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.favorite.domain.MovieFavorite;
import com.ceos23.cgv_clone.favorite.domain.TheaterFavorite;
import com.ceos23.cgv_clone.favorite.dto.response.FavoriteResponse;
import com.ceos23.cgv_clone.favorite.repository.MovieFavoriteRepository;
import com.ceos23.cgv_clone.favorite.repository.TheaterFavoriteRepository;
import com.ceos23.cgv_clone.movie.domain.Movie;
import com.ceos23.cgv_clone.movie.repository.MovieRepository;
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

    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;
    private final MovieRepository movieRepository;
    private final TheaterFavoriteRepository theaterFavoriteRepository;
    private final MovieFavoriteRepository movieFavoriteRepository;

    // 영화관 찜
    @Transactional
    public FavoriteResponse toggleFavoriteTheater(Long userId, Long theaterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.THEATER_NOT_FOUND));

        // 1-1. 이미 자주 가는 곳으로 되어 있을 경우에는 찜 해제
        if (theaterFavoriteRepository.existsByUserAndTheater(user, theater)) {
            theaterFavoriteRepository.deleteByUserAndTheater(user, theater);
            return FavoriteResponse.of(false);
        }
        // 1-2. 아닐 경우
        else {
            // 1-2-1. 자주가는 영화관 5개 초과일 경우 에러 반환
            if (theaterFavoriteRepository.countByUser(user) >= 5) {
                throw new CustomException(ErrorCode.FAVORITE_THEATER_LIMIT_EXCEEDED);
            }

            // 1-2-2. 아닐 경우 저장
            TheaterFavorite favorite = TheaterFavorite.builder()
                    .user(user)
                    .theater(theater)
                    .build();

            theaterFavoriteRepository.save(favorite);
            return FavoriteResponse.of(true);
        }
    }

    // 영화 찜
    @Transactional
    public FavoriteResponse toggleFavoriteMovie(Long userId, Long movieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        // 이미 영화 찜이 되어 있으면 해제
        if (movieFavoriteRepository.existsByUserAndMovie(user, movie)) {
            movieFavoriteRepository.deleteByUserAndMovie(user, movie);
            return FavoriteResponse.of(false);

        } else {
            // 아닐 경우 저장
            MovieFavorite favorite = MovieFavorite.builder()
                    .user(user)
                    .movie(movie)
                    .build();

            movieFavoriteRepository.save(favorite);
            return FavoriteResponse.of(true);
        }
    }
}
