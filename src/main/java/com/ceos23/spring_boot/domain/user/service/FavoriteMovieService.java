package com.ceos23.spring_boot.domain.user.service;

import com.ceos23.spring_boot.domain.movie.entity.Movie;
import com.ceos23.spring_boot.domain.movie.repository.MovieRepository;
import com.ceos23.spring_boot.domain.user.dto.FavoriteMovieInfo;
import com.ceos23.spring_boot.domain.user.entity.FavoriteMovie;
import com.ceos23.spring_boot.domain.user.entity.User;
import com.ceos23.spring_boot.domain.user.repository.FavoriteMovieRepository;
import com.ceos23.spring_boot.domain.user.repository.UserRepository;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
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

    public List<FavoriteMovieInfo> findFavoriteMovies(String email) {
        if (!userRepository.existsByEmailAndDeletedAtIsNull(email)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        List<FavoriteMovie> favoriteMovies = favoriteMovieRepository.findAllByUserEmail(email);

        return favoriteMovies.stream()
                .map(FavoriteMovieInfo::from)
                .toList();
    }

    @Transactional
    public boolean toggleFavorite(String email, Long movieId) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        return favoriteMovieRepository.findByUserAndMovie(user, movie)
                .map(fm -> {
                    favoriteMovieRepository.delete(fm);
                    return false;
                })
                .orElseGet(() -> {
                    favoriteMovieRepository.save(
                            FavoriteMovie.builder()
                                    .user(user)
                                    .movie(movie)
                                    .build());
                    return true;
                });
    }
}
