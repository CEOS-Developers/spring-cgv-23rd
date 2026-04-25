package com.ceos23.spring_boot.cgv.repository.reservation;

import com.ceos23.spring_boot.cgv.domain.reservation.Reservation;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByUserId(Long userId);

    Optional<Reservation> findByIdAndUserId(Long reservationId, Long userId);

    Optional<Reservation> findByPaymentIdAndUserId(String paymentId, Long userId);

    List<Reservation> findAllByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime expiresAt);
}
