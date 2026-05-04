package com.ceos23.spring_boot.cgv.repository.movie;

import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select screening
            from Screening screening
            where screening.id = :screeningId
            """)
    Optional<Screening> findByIdWithPessimisticLock(@Param("screeningId") Long screeningId);

    @EntityGraph(attributePaths = {"movie", "screen", "screen.cinema", "screen.seatLayout"})
    List<Screening> findAllByOrderByStartTimeAsc();

    @EntityGraph(attributePaths = {"movie", "screen", "screen.cinema", "screen.seatLayout"})
    List<Screening> findAllByMovieIdOrderByStartTimeAsc(Long movieId);

    @EntityGraph(attributePaths = {"movie", "screen", "screen.cinema", "screen.seatLayout"})
    List<Screening> findAllByScreenCinemaIdOrderByStartTimeAsc(Long cinemaId);

    @EntityGraph(attributePaths = {"movie", "screen", "screen.cinema", "screen.seatLayout"})
    List<Screening> findAllByMovieIdAndScreenCinemaIdOrderByStartTimeAsc(Long movieId, Long cinemaId);

    @EntityGraph(attributePaths = {"movie", "screen", "screen.cinema", "screen.seatLayout"})
    @Query("""
            select screening
            from Screening screening
            where screening.id = :screeningId
            """)
    Optional<Screening> findByIdWithDetails(@Param("screeningId") Long screeningId);
}
