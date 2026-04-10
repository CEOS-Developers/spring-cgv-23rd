package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.FavoriteMovie;
import com.ceos23.spring_boot.domain.Movie;
import com.ceos23.spring_boot.domain.User;
import com.ceos23.spring_boot.dto.FavoriteMovieResponse;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.repository.FavoriteMovieRepository;
import com.ceos23.spring_boot.repository.MovieRepository;
import com.ceos23.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteMovieService {

    private final FavoriteMovieRepository favoriteMovieRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @Transactional
    public FavoriteMovieResponse createFavoriteMovie(Long userId, Long movieId) {
        validateId(userId);
        validateId(movieId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        if (favoriteMovieRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new CustomException(ErrorCode.FAVORITE_MOVIE_ALREADY_EXISTS);
        }

        FavoriteMovie favoriteMovie = FavoriteMovie.of(user, movie);
        FavoriteMovie savedFavoriteMovie = favoriteMovieRepository.save(favoriteMovie);

        return FavoriteMovieResponse.from(savedFavoriteMovie);
    }

    @Transactional
    public void deleteFavoriteMovie(Long userId, Long movieId) {
        validateId(userId);
        validateId(movieId);

        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        if (!movieRepository.existsById(movieId)) {
            throw new CustomException(ErrorCode.MOVIE_NOT_FOUND);
        }

        if (!favoriteMovieRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new CustomException(ErrorCode.FAVORITE_MOVIE_NOT_FOUND);
        }

        favoriteMovieRepository.deleteByUserIdAndMovieId(userId, movieId);
    }

    public List<FavoriteMovieResponse> getFavoriteMovies(Long userId) {
        validateId(userId);

        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return favoriteMovieRepository.findAllByUserId(userId).stream()
                .map(FavoriteMovieResponse::from)
                .toList();
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id는 1 이상이어야 합니다.");
        }
    }
}