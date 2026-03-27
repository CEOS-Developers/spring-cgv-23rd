package com.cgv.spring_boot.domain.movie.service;

import com.cgv.spring_boot.domain.movie.dto.request.MovieCreateRequest;
import com.cgv.spring_boot.domain.movie.dto.response.MovieResponse;
import com.cgv.spring_boot.domain.movie.entity.Movie;
import com.cgv.spring_boot.domain.movie.entity.MovieWish;
import com.cgv.spring_boot.domain.movie.repository.MovieRepository;
import com.cgv.spring_boot.domain.movie.repository.MovieWishRepository;
import com.cgv.spring_boot.domain.user.entity.User;
import com.cgv.spring_boot.domain.user.repository.UserRepository;
import com.cgv.spring_boot.domain.movie.exception.MovieErrorCode;
import com.cgv.spring_boot.domain.user.exception.UserErrorCode;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieWishRepository movieWishRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long saveMovie(MovieCreateRequest request) {
        Movie movie = request.toEntity();
        return movieRepository.save(movie).getId();
    }

    public List<MovieResponse> findAllMovies() {
        return movieRepository.findAll().stream()
                .map(MovieResponse::from)
                .toList();
    }

    public MovieResponse findMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MovieErrorCode.MOVIE_NOT_FOUND));
        return MovieResponse.from(movie);
    }

    @Transactional
    public void deleteMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MovieErrorCode.MOVIE_NOT_FOUND));
        movieRepository.delete(movie);
    }

    @Transactional
    public Long wishMovie(Long userId, Long movieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new BusinessException(MovieErrorCode.MOVIE_NOT_FOUND));

        if (movieWishRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new BusinessException(MovieErrorCode.MOVIE_ALREADY_WISHED);
        }

        MovieWish movieWish = MovieWish.builder()
                .user(user)
                .movie(movie)
                .build();

        return movieWishRepository.save(movieWish).getId();
    }
}
