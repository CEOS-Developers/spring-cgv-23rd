package com.ceos.spring_cgv_23rd.domain.reservation.service;

import com.ceos.spring_cgv_23rd.domain.reservation.dto.ReservationRequestDTO;
import com.ceos.spring_cgv_23rd.domain.reservation.dto.ReservationResponseDTO;
import com.ceos.spring_cgv_23rd.domain.reservation.entity.Reservation;
import com.ceos.spring_cgv_23rd.domain.reservation.entity.ReservationSeat;
import com.ceos.spring_cgv_23rd.domain.reservation.enums.ReservationStatus;
import com.ceos.spring_cgv_23rd.domain.reservation.exception.ReservationErrorCode;
import com.ceos.spring_cgv_23rd.domain.reservation.repository.ReservationRepository;
import com.ceos.spring_cgv_23rd.domain.reservation.repository.ReservationSeatRepository;
import com.ceos.spring_cgv_23rd.domain.screening.entity.Screening;
import com.ceos.spring_cgv_23rd.domain.screening.exception.ScreeningErrorCode;
import com.ceos.spring_cgv_23rd.domain.screening.repository.ScreeningRepository;
import com.ceos.spring_cgv_23rd.domain.theater.entity.Seat;
import com.ceos.spring_cgv_23rd.domain.theater.repository.SeatRepository;
import com.ceos.spring_cgv_23rd.domain.user.entity.User;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public ReservationResponseDTO.ReservationDetailResponseDTO createReservation(Long userId, ReservationRequestDTO.CreateReservationRequestDTO request) {

        // TODO : 주석 제거
        // 유저 조회
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));
        User user = User.builder()
                .id(userId)
                .build();


        // 상영 스케줄 조회
        Screening screening = screeningRepository.findWithDetailsById(request.screeningId())
                .orElseThrow(() -> new GeneralException(ScreeningErrorCode.SCREENING_NOT_FOUND));

        // 좌석 조회
        List<Seat> seats = seatRepository.findAllByIdIn(request.seatIds());
        if (seats.size() != request.seatIds().size()) {
            throw new GeneralException(ReservationErrorCode.SEAT_NOT_FOUND);
        }

        // 이미 예약된 좌석인지 확인
        List<Long> reservedSeatIds = reservationSeatRepository.findReservedSeatIdsByScreeningId(screening.getId());
        boolean reserved = request.seatIds().stream()
                .anyMatch(reservedSeatIds::contains);
        if (reserved) {
            throw new GeneralException(ReservationErrorCode.SEAT_ALREADY_RESERVED);
        }

        // 남은 좌석 차감
        screening.decreaseRemainingSeats(seats.size());

        // 예매 생성
        Reservation reservation = Reservation.createReservation(user, screening, seats.size());

        reservationRepository.save(reservation);

        // 예매 좌석 생성
        List<ReservationSeat> reservationSeats = seats.stream()
                .map(seat -> ReservationSeat.createReservationSeat(reservation, seat))
                .toList();

        reservationSeatRepository.saveAll(reservationSeats);


        return ReservationResponseDTO.ReservationDetailResponseDTO.of(reservation, reservationSeats);
    }

    @Override
    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {

        // TODO : 주석 제거
        // 유저 조회
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));
        User user = User.builder()
                .id(userId)
                .build();

        // 예매 조회
        Reservation reservation = reservationRepository.findWithScreeningById(reservationId)
                .orElseThrow(() -> new GeneralException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        // TODO: 주석 제거
        // 본인 예약인지 확인
//        if (!user.equals(reservation.getUser())) {
//            throw new GeneralException(ReservationErrorCode.RESERVATION_NOT_FOUND);
//        }

        // 이미 취소된 예약인지 확인
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new GeneralException(ReservationErrorCode.ALREADY_CANCELLED);
        }

        // 예매 좌석 수 조회
        List<ReservationSeat> reservationSeats = reservationSeatRepository.findByReservationId(reservation.getId());

        // 남은 좌석 수 복구
        reservation.getScreening().increaseRemainingSeats(reservationSeats.size());

        // 예매 취소
        reservation.cancel();
    }
}
