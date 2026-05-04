package com.ceos23.spring_boot.domain.reservation.facade;

import com.ceos23.spring_boot.domain.reservation.dto.ReservationCreateCommand;
import com.ceos23.spring_boot.domain.reservation.dto.ReservationInfo;
import com.ceos23.spring_boot.domain.reservation.entity.Reservation;
import com.ceos23.spring_boot.domain.reservation.entity.ReservationStatus;
import com.ceos23.spring_boot.domain.reservation.service.ReservationService;
import com.ceos23.spring_boot.global.lock.RedisLockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ReservationLockFacade {
    private final RedisLockManager redisLockManager;
    private final ReservationService reservationService;

    public ReservationInfo createReservationWithLock(ReservationCreateCommand command) {
        List<Long> seatIds = command.seatIds().stream()
                .sorted()
                .toList();

        List<String> lockKeys = seatIds.stream()
                .map(seatId
                        -> "lock:schedule:" + command.scheduleId() +":seat:" + seatId)
                .toList();

        return redisLockManager.executeWithLock(
                lockKeys,
                0,
                2,
                TimeUnit.SECONDS,
                () -> reservationService.createReservation(command)
        );
    }
}
