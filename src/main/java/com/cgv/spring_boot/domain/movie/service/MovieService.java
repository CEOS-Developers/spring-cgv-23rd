package com.cgv.spring_boot.domain.movie.service;

import com.cgv.spring_boot.domain.movie.dto.request.MovieCreateRequest;
import com.cgv.spring_boot.domain.movie.dto.response.MovieResponse;
import com.cgv.spring_boot.domain.movie.entity.Movie;
import com.cgv.spring_boot.domain.movie.entity.MovieWish;
import com.cgv.spring_boot.domain.movie.exception.MovieErrorCode;
import com.cgv.spring_boot.domain.movie.repository.MovieRepository;
import com.cgv.spring_boot.domain.movie.repository.MovieWishRepository;
import com.cgv.spring_boot.domain.user.entity.User;
import com.cgv.spring_boot.domain.user.repository.UserRepository;
import com.cgv.spring_boot.domain.user.exception.UserErrorCode;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieWishRepository movieWishRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(cacheNames = "movies", allEntries = true)
    public Long saveMovie(MovieCreateRequest request) {
        Movie movie = request.toEntity();
        Long movieId = movieRepository.save(movie).getId();
        log.info("movie created. movieId={}, title={}", movieId, movie.getTitle());
        log.info("cache evicted. cacheName=movies, scope=all");
        return movieId;
    }

    @Cacheable(cacheNames = "movies", key = "'all'")
    public List<MovieResponse> findAllMovies() {
        log.debug("movie list cache miss. cacheName=movies, key=all");
        return movieRepository.findAll().stream()
                .map(MovieResponse::from)
                .toList();
    }

    @Cacheable(cacheNames = "movie", key = "#id")
    public MovieResponse findMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("movie lookup failed. movieId={}", id);
                    return new BusinessException(MovieErrorCode.MOVIE_NOT_FOUND);
                });
        log.debug("movie cache miss. cacheName=movie, key={}", id);
        return MovieResponse.from(movie);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "movies", allEntries = true),
            @CacheEvict(cacheNames = "movie", key = "#id")
    })
    public void deleteMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("movie delete failed. movieId={}", id);
                    return new BusinessException(MovieErrorCode.MOVIE_NOT_FOUND);
                });
        movieRepository.delete(movie);
        log.info("movie deleted. movieId={}, title={}", id, movie.getTitle());
        log.info("cache evicted. cacheNames=movies,movie, movieId={}", id);
    }

    @Transactional
    public Long wishMovie(Long userId, Long movieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new BusinessException(MovieErrorCode.MOVIE_NOT_FOUND));

        if (movieWishRepository.existsByUserIdAndMovieId(userId, movieId)) {
            log.warn("movie wish duplicated. userId={}, movieId={}", userId, movieId);
            throw new BusinessException(MovieErrorCode.MOVIE_ALREADY_WISHED);
        }

        MovieWish movieWish = MovieWish.builder()
                .user(user)
                .movie(movie)
                .build();

        Long movieWishId = movieWishRepository.save(movieWish).getId();
        log.info("movie wished. userId={}, movieId={}, movieWishId={}", userId, movieId, movieWishId);
        return movieWishId;
    }
}
