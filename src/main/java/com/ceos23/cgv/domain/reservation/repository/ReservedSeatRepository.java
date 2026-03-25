package com.ceos23.cgv.domain.reservation.repository;

import com.ceos23.cgv.domain.reservation.entity.ReservedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservedSeatRepository extends JpaRepository<ReservedSeat, Long> {
    // 특정 상영 일정에 이미 예매된 좌석(회색 처리용) 목록 조회
    List<ReservedSeat> findByScreeningId(Long screeningId);

    // 특정 예매 ID에 해당하는 모든 예매 좌석 삭제
    void deleteAllByReservationId(Long reservationId);
}