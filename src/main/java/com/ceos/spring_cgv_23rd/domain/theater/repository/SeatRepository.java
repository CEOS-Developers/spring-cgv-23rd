package com.ceos.spring_cgv_23rd.domain.theater.repository;

import com.ceos.spring_cgv_23rd.domain.theater.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findAllByIdIn(List<Long> seatIds);
}
