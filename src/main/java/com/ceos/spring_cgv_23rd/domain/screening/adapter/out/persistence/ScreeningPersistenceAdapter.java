package com.ceos.spring_cgv_23rd.domain.screening.adapter.out.persistence;

import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity.MovieEntity;
import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.repository.MovieJpaRepository;
import com.ceos.spring_cgv_23rd.domain.screening.adapter.out.persistence.mapper.ScreeningPersistenceMapper;
import com.ceos.spring_cgv_23rd.domain.screening.adapter.out.persistence.repository.ScreeningJpaRepository;
import com.ceos.spring_cgv_23rd.domain.screening.application.dto.result.HallInfoResult;
import com.ceos.spring_cgv_23rd.domain.screening.application.dto.result.MovieInfoResult;
import com.ceos.spring_cgv_23rd.domain.screening.application.port.out.ScreeningPersistencePort;
import com.ceos.spring_cgv_23rd.domain.screening.domain.Screening;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.HallEntity;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository.HallJpaRepository;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository.TheaterJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScreeningPersistenceAdapter implements ScreeningPersistencePort {

    private final ScreeningJpaRepository screeningJpaRepository;
    private final HallJpaRepository hallJpaRepository;
    private final MovieJpaRepository movieJpaRepository;
    private final TheaterJpaRepository theaterJpaRepository;
    private final ScreeningPersistenceMapper mapper;

    @Override
    public List<Screening> findScreeningByMovieAndTheaterAndDate(Long movieId, Long theaterId, LocalDate date) {
        return screeningJpaRepository.findByMovieAndTheaterAndDate(movieId, theaterId, date).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Screening> findScreeningByTheaterAndDate(Long theaterId, LocalDate date) {
        return screeningJpaRepository.findByTheaterAndDate(theaterId, date).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Map<Long, HallInfoResult> findHallInfoByIds(List<Long> hallIds) {
        return hallJpaRepository.findAllWithHallTypeByIdIn(hallIds).stream()
                .collect(Collectors.toMap(
                        HallEntity::getId,
                        h -> new HallInfoResult(h.getId(), h.getName(), h.getHallType().getName())
                ));
    }

    @Override
    public Map<Long, MovieInfoResult> findMovieInfoByIds(List<Long> movieIds) {
        return movieJpaRepository.findAllById(movieIds).stream()
                .collect(Collectors.toMap(
                        MovieEntity::getId,
                        m -> new MovieInfoResult(m.getId(), m.getTitle(), m.getPosterUrl(), m.getAgeRating())
                ));
    }

    @Override
    public boolean existsMovieById(Long movieId) {
        return movieJpaRepository.existsById(movieId);
    }

    @Override
    public boolean existsTheaterById(Long theaterId) {
        return theaterJpaRepository.existsById(theaterId);
    }
}
