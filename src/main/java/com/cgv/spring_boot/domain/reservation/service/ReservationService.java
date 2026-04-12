package com.cgv.spring_boot.domain.reservation.service;

import com.cgv.spring_boot.domain.payment.dto.response.PaymentResponse;
import com.cgv.spring_boot.domain.payment.service.PaymentService;
import com.cgv.spring_boot.domain.reservation.dto.ReservationRequest;
import com.cgv.spring_boot.domain.reservation.entity.Reservation;
import com.cgv.spring_boot.domain.reservation.entity.ReservationStatus;
import com.cgv.spring_boot.domain.reservation.entity.ReservedSeat;
import com.cgv.spring_boot.domain.reservation.entity.SeatPosition;
import com.cgv.spring_boot.domain.reservation.repository.ReservationRepository;
import com.cgv.spring_boot.domain.reservation.repository.ReservedSeatRepository;
import com.cgv.spring_boot.domain.schedule.entity.Schedule;
import com.cgv.spring_boot.domain.schedule.repository.ScheduleRepository;
import com.cgv.spring_boot.domain.user.entity.User;
import com.cgv.spring_boot.domain.user.repository.UserRepository;
import com.cgv.spring_boot.domain.reservation.exception.ReservationErrorCode;
import com.cgv.spring_boot.domain.schedule.exception.ScheduleErrorCode;
import com.cgv.spring_boot.domain.user.exception.UserErrorCode;
import com.cgv.spring_boot.global.error.code.GlobalErrorCode;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ReservedSeatRepository reservedSeatRepository;
    private final PaymentService paymentService;

    /**
     * 예매 좌석 선점
     */
    @Transactional
    public Long reserve(Long userId, ReservationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(request.scheduleId())
                .orElseThrow(() -> new BusinessException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

        List<SeatPosition> seatPositions = request.seats().stream()
                .map(seatRequest -> new SeatPosition(seatRequest.seatRow(), seatRequest.seatCol()))
                .toList();

        // 상영관 범위를 벗어난 좌석인지 확인
        validateSeatRange(schedule, seatPositions);
        // 같은 요청 안에서 중복 좌석을 선택했는지 확인
        validateDuplicateSeatsInRequest(seatPositions);
        // 이미 다른 예매가 선점한 좌석인지 확인
        validateAlreadyReservedSeats(schedule, seatPositions);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        Reservation reservation = Reservation.createPending(user, schedule, expiresAt);

        Reservation savedReservation = reservationRepository.save(reservation);

        List<ReservedSeat> reservedSeats = seatPositions.stream()
                .map(seatPosition -> ReservedSeat.create(savedReservation, schedule, seatPosition))
                .toList();

        // DB 유니크 제약조건 검증
        try {
            reservedSeatRepository.saveAllAndFlush(reservedSeats);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ReservationErrorCode.ALREADY_RESERVED_SEAT);
        }

        return savedReservation.getId();
    }

    /** 좌석 범위 검증 */
    private void validateSeatRange(Schedule schedule, List<SeatPosition> seatPositions) {
        seatPositions.forEach(seatPosition -> seatPosition.validateAgainst(schedule.getHall().getHallType()));
    }

    /** 요청 내 중복 좌석 검증 */
    private void validateDuplicateSeatsInRequest(List<SeatPosition> seatPositions) {
        Set<SeatPosition> uniqueSeats = Set.copyOf(seatPositions);
        if (uniqueSeats.size() != seatPositions.size()) {
            throw new BusinessException(ReservationErrorCode.ALREADY_RESERVED_SEAT);
        }
    }

    /** 기예약 좌석 검증 */
    private void validateAlreadyReservedSeats(Schedule schedule, List<SeatPosition> seatPositions) {
        boolean alreadyReserved = seatPositions.stream()
                .anyMatch(seatPosition -> reservedSeatRepository.existsByScheduleIdAndSeatRowAndSeatCol(
                        schedule.getId(),
                        seatPosition.seatRow(),
                        seatPosition.seatCol())
                );

        if (alreadyReserved) {
            throw new BusinessException(ReservationErrorCode.ALREADY_RESERVED_SEAT);
        }
    }

    /**
     * 예매 결제 및 확정
     */
    @Transactional
    public PaymentResponse pay(Long userId, Long reservationId) {
        Reservation reservation = getOwnedReservation(userId, reservationId);
        validateReservationPayable(reservation);

        long seatCount = reservedSeatRepository.countByReservationId(reservationId);
        Schedule schedule = reservation.getSchedule();
        int totalAmount = Math.toIntExact(seatCount * schedule.getTicketPrice());
        String orderName = schedule.getMovie().getTitle() + " 예매";
        String customData = "{\"reservationId\":" + reservationId + ",\"seatCount\":" + seatCount + "}";

        PaymentResponse response = paymentService.payReservation(reservation, totalAmount, orderName, customData);
        reservation.confirm();
        return response;
    }

    /** 결제 가능 예약 검증 */
    private void validateReservationPayable(Reservation reservation) {
        if (reservation.getExpiresAt().isBefore(LocalDateTime.now())) {
            reservation.expire();
            reservedSeatRepository.deleteByReservation(reservation);
            throw new BusinessException(ReservationErrorCode.RESERVATION_EXPIRED);
        }

        if (reservation.getStatus() != ReservationStatus.PENDING_PAYMENT) {
            throw new BusinessException(ReservationErrorCode.INVALID_RESERVATION_STATUS);
        }
    }

    /**
     * 예매 취소
     */
    @Transactional
    public void cancel(Long userId, Long reservationId) {
        Reservation reservation = getOwnedReservation(userId, reservationId);
        reservation.cancel();

        reservedSeatRepository.deleteByReservation(reservation);
    }

    /** 본인 예약 조회 */
    private Reservation getOwnedReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessException(GlobalErrorCode.FORBIDDEN_ACCESS);
        }

        return reservation;
    }
}
