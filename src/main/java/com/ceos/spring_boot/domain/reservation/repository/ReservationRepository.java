package com.ceos.spring_boot.domain.reservation.repository;

import com.ceos.spring_boot.domain.cinema.entity.ScreenType;
import com.ceos.spring_boot.domain.cinema.entity.Seat;
import com.ceos.spring_boot.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}

