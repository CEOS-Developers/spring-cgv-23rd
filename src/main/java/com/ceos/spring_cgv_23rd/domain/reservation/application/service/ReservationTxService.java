package com.ceos.spring_cgv_23rd.domain.reservation.application.service;

import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.ReservationPersistencePort;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.ScreeningPort;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationTxService {

    private final ReservationPersistencePort reservationPersistencePort;
    private final ScreeningPort screeningPort;


    @Transactional
    public Reservation persistConfirmed(Long userId, Long screeningId, int price,
                                        String paymentId, List<Long> seatIds) {
        // 상영 잔여 좌석 수 차감
        screeningPort.decreaseScreeningSeats(screeningId, seatIds.size());

        // 예매 객체 생성
        Reservation reservation = Reservation.createReservation(userId, screeningId, price, paymentId, seatIds);

        // 예매 정보 DB에 저장
        return reservationPersistencePort.saveReservation(reservation);
    }


    @Transactional
    public void applyCancellation(Reservation reservation) {
        // 예매 상태 취소 처리
        reservation.cancel();

        // 상영 잔여 좌석 수 복구
        screeningPort.increaseScreeningSeats(reservation.getScreeningId(), reservation.getSeatCount());

        // 취소된 예매 상태 DB 반영
        reservationPersistencePort.updateReservationStatus(reservation.getId(), reservation.getStatus());
    }
}
