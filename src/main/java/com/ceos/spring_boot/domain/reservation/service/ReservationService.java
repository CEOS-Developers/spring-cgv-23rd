package com.ceos.spring_boot.domain.reservation.service;

import com.ceos.spring_boot.domain.cinema.entity.ScreenType;
import com.ceos.spring_boot.domain.cinema.entity.Seat;
import com.ceos.spring_boot.domain.cinema.repository.SeatRepository;
import com.ceos.spring_boot.domain.reservation.dto.ReservationRequest;
import com.ceos.spring_boot.domain.reservation.dto.ReservationResponse;
import com.ceos.spring_boot.domain.reservation.entity.Reservation;
import com.ceos.spring_boot.domain.reservation.entity.ReservationSeat;
import com.ceos.spring_boot.domain.reservation.entity.ReservationStatus;
import com.ceos.spring_boot.domain.reservation.repository.ReservationRepository;
import com.ceos.spring_boot.domain.reservation.repository.ReservationSeatRepository;
import com.ceos.spring_boot.domain.schedule.entity.Schedule;
import com.ceos.spring_boot.domain.schedule.repository.ScheduleRepository;
import com.ceos.spring_boot.domain.user.entity.User;
import com.ceos.spring_boot.domain.user.repository.UserRepository;
import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    public ReservationResponse createReservation(Long userId, ReservationRequest request) {

        // 락 키 생성 및 정렬 (데드락 방지)
        List<String> lockKeys = request.seats().stream()
                .map(s -> "lock:" + request.scheduleId() + ":" + s.row().toUpperCase() + "-" + s.column())
                .sorted()
                .toList();

        RLock multiLock = redissonClient.getMultiLock(
                lockKeys.stream().map(redissonClient::getLock).toArray(RLock[]::new)
        );

        try {
            // 분산 락 획득 시도
            boolean isLocked = multiLock.tryLock(5, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new BusinessException(ErrorCode.ALREADY_RESERVED_SEAT_ERROR);
            }

            // 트랜잭션 내 비즈니스 로직 수행
            return transactionTemplate.execute(status -> {
                try {
                    // 사용자(Proxy) 및 일정 조회
                    User user = userRepository.getReferenceById(userId);
                    Schedule schedule = scheduleRepository.findByIdWithDetails(request.scheduleId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND_ERROR));

                    ScreenType screenType = schedule.getScreen().getScreenType();

                    // 좌석 조회 및 검증
                    List<String> seatIdentifiers = request.seats().stream()
                            .map(req -> req.row().toUpperCase() + "-" + req.column())
                            .toList();

                    List<Seat> seats = reservationSeatRepository.findByScreenTypeAndSeatIdentifiers(screenType, seatIdentifiers);

                    if (seats.size() != request.seats().size()) {
                        throw new BusinessException(ErrorCode.SEAT_NOT_FOUND_ERROR);
                    }

                    List<Long> seatIds = seats.stream().map(Seat::getId).toList();

                    if (reservationSeatRepository.existsByScheduleIdAndSeatIdIn(schedule.getId(), seatIds)) {
                        throw new BusinessException(ErrorCode.ALREADY_RESERVED_SEAT_ERROR);
                    }

                    // 예매 생성 및 저장
                    Reservation reservation = Reservation.create(user, schedule);
                    reservationRepository.save(reservation);

                    List<ReservationSeat> reservationSeats = seats.stream()
                            .map(seat -> ReservationSeat.create(reservation, seat, schedule))
                            .toList();
                    reservationSeatRepository.saveAll(reservationSeats);

                    // DB 제약 조건 즉시 확인
                    reservationSeatRepository.flush();

                    return ReservationResponse.from(reservation);

                } catch (DataIntegrityViolationException e) {
                    throw new BusinessException(ErrorCode.ALREADY_RESERVED_SEAT_ERROR);
                }
            });

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            if (multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
            }
        }
    }

    @Transactional
    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND_ERROR));

        reservation.confirm();

        log.info("[Reservation Confirmed] 영화 예매 확정 완료 - 예약 ID: {}", reservationId);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND_ERROR));

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELED_RESERVATION_ERROR);
        }

        reservation.cancel();
        reservationSeatRepository.deleteByReservationId(reservationId);
    }

    @Transactional
    public void rollbackPreOccupiedReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND_ERROR));

        reservation.cancel();
        reservationSeatRepository.deleteByReservationId(reservationId);
        log.info("[Rollback] 결제 실패로 인한 좌석 선점 해제 완료 - 예약 ID: {}", reservationId);
    }
}