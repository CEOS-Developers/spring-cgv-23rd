package com.ceos.spring_cgv_23rd.domain.movie.application.service;

import com.ceos.spring_cgv_23rd.domain.movie.application.dto.command.ToggleMovieLikeCommand;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.ToggleMovieLikeResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.port.in.ToggleMovieLikeUseCase;
import com.ceos.spring_cgv_23rd.domain.movie.application.port.out.MoviePersistencePort;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieLike;
import com.ceos.spring_cgv_23rd.domain.movie.exception.MovieErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieCommandService implements ToggleMovieLikeUseCase {

    private final MoviePersistencePort moviePersistencePort;

    @Override
    @Transactional
    public ToggleMovieLikeResult execute(ToggleMovieLikeCommand command) {

        // 영화 조회
        if (!moviePersistencePort.existsMovieById(command.movieId())) {
            throw new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND);
        }

        Optional<MovieLike> existingLike = moviePersistencePort
                .findMovieLikeByUserAndMovie(command.userId(), command.movieId());

        boolean liked;
        if (existingLike.isPresent()) {
            // 찜 취소
            moviePersistencePort.deleteMovieLike(existingLike.get());
            liked = false;
        } else {
            // 찜 등록
            MovieLike movieLike = MovieLike.create(command.userId(), command.movieId());
            moviePersistencePort.saveMovieLike(movieLike);
            liked = true;
        }

        return new ToggleMovieLikeResult(command.movieId(), liked);
    }
}
