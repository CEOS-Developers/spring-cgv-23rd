package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.Reservation;
import com.ceos23.spring_boot.domain.Screening;
import com.ceos23.spring_boot.domain.Seat;
import com.ceos23.spring_boot.domain.User;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.repository.ReservationRepository;
import com.ceos23.spring_boot.repository.ScreeningRepository;
import com.ceos23.spring_boot.repository.SeatRepository;
import com.ceos23.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public Reservation reserve(Long userId, Long screeningId, Long seatId) {
        ensureSeatNotReserved(screeningId, seatId);

        User user = loadUser(userId);
        Screening screening = loadScreening(screeningId);
        Seat seat = loadSeat(seatId);

        Reservation reservation = Reservation.create(user, screening, seat);

        try {
            return reservationRepository.saveAndFlush(reservation);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }
    }

    @Transactional
    public void cancel(Long reservationId) {
        validateReservationExists(reservationId);
        reservationRepository.deleteById(reservationId);
    }

    private void ensureSeatNotReserved(Long screeningId, Long seatId) {
        boolean exists = reservationRepository.existsByScreeningIdAndSeatId(screeningId, seatId);

        if (exists) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Screening loadScreening(Long screeningId) {
        return screeningRepository.findById(screeningId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCREENING_NOT_FOUND));
    }

    private Seat loadSeat(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
    }

    private void validateReservationExists(Long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
    }
}