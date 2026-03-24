package com.ceos.spring_boot.domain.reservation.repository;

import com.ceos.spring_boot.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 특정 사용자의 예매 내역 최신순 조회
    List<Reservation> findByUserIdOrderByReservationDateDesc(Long userId);

}

