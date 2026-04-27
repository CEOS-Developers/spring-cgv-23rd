package com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence;

import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity.MovieEntity;
import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity.MovieLikeEntity;
import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.mapper.MoviePersistenceMapper;
import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.repository.MovieCreditJpaRepository;
import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.repository.MovieJpaRepository;
import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.repository.MovieLikeJpaRepository;
import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.repository.MovieMediaJpaRepository;
import com.ceos.spring_cgv_23rd.domain.movie.application.port.out.MoviePersistencePort;
import com.ceos.spring_cgv_23rd.domain.movie.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MoviePersistenceAdapter implements MoviePersistencePort {

    private final MovieJpaRepository movieJpaRepository;
    private final MovieCreditJpaRepository movieCreditJpaRepository;
    private final MovieMediaJpaRepository movieMediaJpaRepository;
    private final MovieLikeJpaRepository movieLikeJpaRepository;
    private final MoviePersistenceMapper mapper;


    @Override
    public Optional<Movie> findMovieById(Long movieId) {
        return movieJpaRepository.findWithStatisticById(movieId)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsMovieById(Long movieId) {
        return movieJpaRepository.existsById(movieId);
    }

    @Override
    public List<Movie> findMoviesWithStatisticByStatus(MovieStatus status) {
        return mapper.toDomainMovies(movieJpaRepository.findWithStatisticByStatus(status));
    }

    @Override
    public List<Movie> findMoviesWithStatisticByStatusIn(List<MovieStatus> statuses) {
        return mapper.toDomainMovies(movieJpaRepository.findWithStatisticByStatusIn(statuses));
    }


    @Override
    public List<MovieCredit> findCreditsByMovieIdWithContributor(Long movieId) {
        return mapper.toDomainCredits(movieCreditJpaRepository.findByMovieIdWithContributor(movieId));
    }


    @Override
    public List<MovieMedia> findMediasByMovieId(Long movieId) {
        return mapper.toDomainMedias(movieMediaJpaRepository.findByMovieId(movieId));
    }


    @Override
    public Optional<MovieLike> findMovieLikeByUserAndMovie(Long userId, Long movieId) {
        return movieLikeJpaRepository.findByUserIdAndMovieId(userId, movieId)
                .map(mapper::toDomain);
    }

    @Override
    public MovieLike saveMovieLike(MovieLike movieLike) {
        MovieEntity movieEntity = movieJpaRepository.getReferenceById(movieLike.getMovieId());

        MovieLikeEntity saved = movieLikeJpaRepository.save(mapper.toEntity(movieEntity, movieLike.getUserId()));

        return mapper.toDomain(saved);
    }

    @Override
    public void deleteMovieLike(MovieLike movieLike) {
        movieLikeJpaRepository.deleteById(movieLike.getId());
    }
}
