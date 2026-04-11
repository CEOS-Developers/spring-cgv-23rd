package com.ceos.spring_cgv_23rd.domain.screening.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.screening.adapter.out.persistence.entity.ScreeningEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScreeningJpaRepository extends JpaRepository<ScreeningEntity, Long> {

    // 영화별 상영 스케줄 : 영화 + 극장 + 날짜
    @Query("SELECT s from ScreeningEntity s " +
            "JOIN s.hall h " +
            "WHERE s.movie.id = :movieId " +
            "AND h.theater.id = :theaterId " +
            "AND CAST(s.startAt AS date) = :date " +
            "ORDER BY s.startAt")
    List<ScreeningEntity> findByMovieAndTheaterAndDate(Long movieId, Long theaterId, LocalDate date);

    // 극장별 상영 스케줄 : 극장 + 날짜
    @Query("SELECT s FROM ScreeningEntity s " +
            "JOIN s.hall h " +
            "WHERE h.theater.id = :theaterId " +
            "AND CAST(s.startAt AS date) = :date " +
            "ORDER BY s.movie.id, s.startAt")
    List<ScreeningEntity> findByTheaterAndDate(Long theaterId, LocalDate date);

    // TODO 삭제
    @Query("SELECT s FROM ScreeningEntity s " +
            "JOIN FETCH s.movie " +
            "JOIN FETCH  s.hall h " +
            "JOIN FETCH h.theater " +
            "WHERE s.id = :screeningId")
    Optional<ScreeningEntity> findWithDetailsById(Long screeningId);

}
