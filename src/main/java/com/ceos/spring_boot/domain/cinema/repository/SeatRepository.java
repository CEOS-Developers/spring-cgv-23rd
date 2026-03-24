package com.ceos.spring_boot.domain.cinema.repository;

import com.ceos.spring_boot.domain.cinema.entity.ScreenType;
import com.ceos.spring_boot.domain.cinema.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findByScreenTypeAndSeatRowAndSeatCol(ScreenType screenType, String seatRow, Integer seatCol);
}
