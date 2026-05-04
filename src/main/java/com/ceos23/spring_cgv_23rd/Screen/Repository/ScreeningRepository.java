package com.ceos23.spring_cgv_23rd.Screen.Repository;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningWrapperDTO;
import com.ceos23.spring_cgv_23rd.Screen.DTO.ScreeningSearchQueryResultDTO;
import com.ceos23.spring_cgv_23rd.Screen.Domain.CinemaType;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screen;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @Query("""
    select s from Screening s
    join fetch s.movie m
    join fetch s.screen sc
    join fetch sc.theater t
    
    where t = :theater
    and s.startTime >= :start
    and s.startTime < :end
""")
    List<Screening> findByTheaterAndDate(
            @Param("theater") Theater theater,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    select s from Screening s
    join fetch s.screen sc
    join fetch s.movie m
    where sc.theater = :theater
    and m = :movie
    and s.startTime < :now
    """)
    List<Screening> findByTheaterAndMovieToEntity(
            @Param("theater") Theater screenTheater,
            @Param("movie") Movie movie,
            @Param("now") LocalDateTime now);

    @Query("""
        select new com.ceos23.spring_cgv_23rd.Screen.DTO.ScreeningSearchQueryResultDTO(
            sc.cinemaType, s.id, sc.screenName, s.startTime, m.runningTime, sc.seatAmount
        )
        from Screening s
        join s.movie m
        join s.screen sc
        where sc.theater = :theater
        and m = :movie
        and s.startTime >= :start
        and s.startTime < :end
    """)
    List<ScreeningSearchQueryResultDTO> findByTheaterAndMovie(
            @Param("theater") Theater screenTheater,
            @Param("movie") Movie movie,
            @Param("start") LocalDateTime now,
            @Param("end") LocalDateTime end);

    @Query("""
        select new com.ceos23.spring_cgv_23rd.Screen.DTO.ScreeningSearchQueryResultDTO(
            sc.cinemaType, s.id, sc.screenName, s.startTime, m.runningTime, sc.seatAmount
        )
        from Screening s
        join s.movie m
        join s.screen sc
        where sc.theater = :theater
        and m.id in :movies
        and s.startTime < :now
    """)
    List<ScreeningSearchQueryResultDTO> findByTheaterAndMovies(
            @Param("theater") Theater screenTheater,
            @Param("movies") List<Long> movieIds,
            @Param("now") LocalDateTime now);
}
/*
        long screeningId,
        String screenName,
        LocalDateTime startTime,
        int runningTime,
        Boolean morning,
        Boolean evening,
        int totalSeatAmount
 */
