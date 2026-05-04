package com.ceos23.cgv_clone.favorite.service;

import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.favorite.entity.MovieFavorite;
import com.ceos23.cgv_clone.favorite.entity.TheaterFavorite;
import com.ceos23.cgv_clone.favorite.dto.response.FavoriteResponse;
import com.ceos23.cgv_clone.favorite.repository.MovieFavoriteRepository;
import com.ceos23.cgv_clone.favorite.repository.TheaterFavoriteRepository;
import com.ceos23.cgv_clone.movie.entity.Movie;
import com.ceos23.cgv_clone.movie.repository.MovieRepository;
import com.ceos23.cgv_clone.theater.entity.Theater;
import com.ceos23.cgv_clone.theater.repository.TheaterRepository;
import com.ceos23.cgv_clone.user.entity.User;
import com.ceos23.cgv_clone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;
    private final MovieRepository movieRepository;
    private final TheaterFavoriteRepository theaterFavoriteRepository;
    private final MovieFavoriteRepository movieFavoriteRepository;

    private static final int MAX_FAVORITE_THEATER_COUNT = 5;

    @Transactional
    public FavoriteResponse toggleFavoriteTheater(Long userId, Long theaterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.THEATER_NOT_FOUND));

        Optional<TheaterFavorite> favorite = theaterFavoriteRepository.findByUserAndTheater(user, theater);

        if (favorite.isPresent()) {
            theaterFavoriteRepository.delete(favorite.get());
            return FavoriteResponse.of(false);
        }

        if (theaterFavoriteRepository.countByUser(user) >= MAX_FAVORITE_THEATER_COUNT) {
            throw new CustomException(ErrorCode.FAVORITE_THEATER_LIMIT_EXCEEDED);
        }

        TheaterFavorite theaterFavorite = TheaterFavorite.create(user, theater);

        theaterFavoriteRepository.save(theaterFavorite);
        return FavoriteResponse.of(true);

    }

    @Transactional
    public FavoriteResponse toggleFavoriteMovie(Long userId, Long movieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        Optional<MovieFavorite> favorite = movieFavoriteRepository.findByUserAndMovie(user, movie);

        if (favorite.isPresent()) {
            movieFavoriteRepository.delete(favorite.get());
            return FavoriteResponse.of(false);
        }

        MovieFavorite movieFavorite = MovieFavorite.create(user, movie);

        movieFavoriteRepository.save(movieFavorite);
        return FavoriteResponse.of(true);
    }
}
