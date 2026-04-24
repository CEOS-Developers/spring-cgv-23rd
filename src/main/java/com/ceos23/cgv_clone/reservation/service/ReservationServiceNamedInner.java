package com.ceos23.cgv_clone.reservation.service;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.reservation.dto.request.ReservationRequest;
import com.ceos23.cgv_clone.reservation.dto.response.PendingReservationResponse;
import com.ceos23.cgv_clone.reservation.dto.response.ReservationResponse;
import com.ceos23.cgv_clone.reservation.entity.Reservation;
import com.ceos23.cgv_clone.reservation.entity.ReservationSeat;
import com.ceos23.cgv_clone.reservation.entity.ReservationStatus;
import com.ceos23.cgv_clone.reservation.repository.ReservationRepository;
import com.ceos23.cgv_clone.reservation.repository.ReservationSeatRepository;
import com.ceos23.cgv_clone.theater.entity.Schedule;
import com.ceos23.cgv_clone.theater.repository.ScheduleRepository;
import com.ceos23.cgv_clone.user.entity.User;
import com.ceos23.cgv_clone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.List;

import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;
import static java.util.UUID.randomUUID;
import static org.springframework.transaction.annotation.Propagation.*;

@Service
@RequiredArgsConstructor
public class ReservationServiceNamedInner{

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    // 영화 예매
    @Transactional(propagation = REQUIRES_NEW)
    public PendingReservationResponse prepareReservation(Long userId, ReservationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        List<String> seats = request.getSeats();

        validateAgeRestriction(user, schedule.getMovie().getAgeRestriction());
        validateSeats(seats, schedule);

        int totalPrice = schedule.getScreen().getScreenType().getPrice() * request.getSeats().size();
        String paymentId = generatePaymentId();

        Reservation reservation = Reservation.builder()
                .reservedAt(LocalDateTime.now())
                .totalPrice(totalPrice)
                .status(ReservationStatus.PENDING)
                .paymentId(paymentId)
                .user(user)
                .schedule(schedule)
                .build();

        for (String seatStr : request.getSeats()) {
            ReservationSeat seat = ReservationSeat.builder()
                    .seatRow(seatStr.charAt(0))
                    .seatCol(Integer.parseInt(seatStr.substring(1)))
                    .reservation(reservation)
                    .schedule(schedule)
                    .build();
            reservation.addReservationSeat(seat);
        }

        reservationRepository.save(reservation);

        String orderName = buildOrderName(reservation);

        return new PendingReservationResponse(reservation.getId(), orderName, totalPrice, reservation.getReservedAt(), request.getSeats(), schedule.getMovie().getName());
    }

    @Transactional(propagation = REQUIRES_NEW)
    public ReservationResponse confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.confirmReservation();
        return ReservationResponse.from(reservation);
    }

    // 예매 취소
    @Transactional(propagation = REQUIRES_NEW)
    public String cancelReservation(Long userId, Long reservationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        verifyCancel(user, reservation);


        reservation.cancel();
        return reservation.getPaymentId();
    }

    @Transactional(readOnly = true, propagation = REQUIRES_NEW)
    public PendingReservation loadForPayment(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        verifyPendingReservation(userId, reservation);

        String orderName = buildOrderName(reservation);

        return new PendingReservation(reservation.getId(), reservation.getPaymentId(), orderName, reservation.getTotalPrice());
    }

    private void verifyCancel(User user, Reservation reservation) {
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR);
        }

        if (reservation.getStatus().equals(ReservationStatus.CANCELED)) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED_RESERVATION);
        }
    }

    private void verifyPendingReservation(Long userId, Reservation reservation) {
        if (!reservation.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR);
        }
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATUS);
        }
        // 5분 경과 후 스케쥴러가 잡지 못했는데, 다른 유저가 들어와서 결제 시도하면 문제 생기므로 추가 검증.
        if (reservation.getReservedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.RESERVATION_EXPIRED);
        }
    }

    private void validateAgeRestriction(User user, int ageRestriction) {
        if (ageRestriction < 18) return;

        if (user.getBirthdate() == null) {
            throw new CustomException(ErrorCode.USER_BIRTHDATE_NOT_FOUND);
        }

        int userAge = Period.between(user.getBirthdate(), LocalDate.now()).getYears();
        if (userAge < ageRestriction) {
            throw new CustomException(ErrorCode.AGE_RESTRICTED);
        }
    }

    private void validateSeats(List<String> seats, Schedule schedule) {
        if (seats.size() != new HashSet<>(seats).size()) {
            throw new CustomException(ErrorCode.INVALID_SEAT);
        }

        for (String seatStr : seats) {
            // 2-2. 좌석 문자열 검사
            if (seatStr == null || !seatStr.matches("^[A-Z]\\d+$")) {
                throw new CustomException(ErrorCode.INVALID_SEAT);
            }

            char row = seatStr.charAt(0);
            int col = Integer.parseInt(seatStr.substring(1));

            // 2-3. 좌석 범위 검사
            if (row > schedule.getScreen().getScreenType().getMaxRow() || col > schedule.getScreen().getScreenType().getMaxCol()) {
                throw new CustomException(ErrorCode.INVALID_SEAT);
            }

            // 2-4. 좌석 중복 검사
            // ReservationStatus.Canceled가 아닌 것 조회 -> 즉 취소 된 거는 카운트 X
            if (reservationSeatRepository.existsByScheduleAndSeatRowAndSeatColAndReservation_StatusNot(schedule, row, col, ReservationStatus.CANCELED)) {
                throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);
            }
        }
    }

    private String buildOrderName(Reservation reservation) {
        List<String> seats = reservation.getReservationSeats().stream()
                .map(s -> s.getSeatRow() + String.valueOf(s.getSeatCol()))
                .toList();
        return reservation.getSchedule().getMovie().getName() + " " + String.join(", ", seats);
    }

    private String generatePaymentId() {
        return now().format(BASIC_ISO_DATE) + "_" + randomUUID().toString().substring(0, 8);
    }
}
