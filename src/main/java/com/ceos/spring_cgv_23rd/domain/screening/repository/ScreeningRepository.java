package com.ceos.spring_cgv_23rd.domain.screening.repository;

import com.ceos.spring_cgv_23rd.domain.screening.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    // 영화별 상영 스케줄 : 영화 + 극장 + 날짜
    @Query("SELECT s from Screening s " +
            "JOIN FETCH s.hall h " +
            "JOIN FETCH h.hallType " +
            "WHERE s.movie.id = :movieId " +
            "AND h.theater.id = :theaterId " +
            "AND CAST(s.startAt AS date) = :date " +
            "ORDER BY s.startAt")
    List<Screening> findByMovieAndTheaterAndDate(Long movieId, Long theaterId, LocalDate date);

    // 극장별 상영 스케줄 : 극장 + 날짜
    @Query("SELECT s FROM Screening s " +
            "JOIN FETCH s.movie m " +
            "JOIN FETCH s.hall h " +
            "JOIN FETCH h.hallType " +
            "WHERE h.theater.id = :theaterId " +
            "AND CAST(s.startAt AS date) = :date " +
            "ORDER BY m.id, s.startAt")
    List<Screening> findByTheaterAndDate(Long theaterId, LocalDate date);

    @Query("SELECT s FROM Screening s " +
            "JOIN FETCH s.movie " +
            "JOIN FETCH  s.hall h " +
            "JOIN FETCH h.theater " +
            "WHERE s.id = :screeningId")
    Optional<Screening> findWithDetailsById(Long screeningId);

}
