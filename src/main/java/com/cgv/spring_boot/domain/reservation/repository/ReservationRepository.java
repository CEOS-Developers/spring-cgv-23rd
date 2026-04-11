package com.cgv.spring_boot.domain.reservation.repository;

import com.cgv.spring_boot.domain.reservation.entity.Reservation;
import com.cgv.spring_boot.domain.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByStatusAndExpiresAtBefore(
            ReservationStatus status,
            LocalDateTime time
    );
}
