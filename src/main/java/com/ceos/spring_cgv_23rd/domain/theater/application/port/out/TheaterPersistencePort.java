package com.ceos.spring_cgv_23rd.domain.theater.application.port.out;

import com.ceos.spring_cgv_23rd.domain.theater.domain.Theater;
import com.ceos.spring_cgv_23rd.domain.theater.domain.TheaterLike;

import java.util.List;
import java.util.Optional;

public interface TheaterPersistencePort {

    // Theater
    List<Theater> findAllTheaters();

    Optional<Theater> findTheaterById(Long theaterId);

    boolean existsTheaterById(Long theaterId);
    

    // TheaterLike
    Optional<TheaterLike> findTheaterLikeByUserAndTheater(Long userId, Long theaterId);

    TheaterLike saveTheaterLike(TheaterLike theaterLike);

    void deleteTheaterLike(TheaterLike theaterLike);
}
