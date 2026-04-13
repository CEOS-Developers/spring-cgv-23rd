package com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence;

import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ScreeningInfoResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.ScreeningPort;
import com.ceos.spring_cgv_23rd.domain.reservation.exception.ReservationErrorCode;
import com.ceos.spring_cgv_23rd.domain.screening.adapter.out.persistence.entity.ScreeningEntity;
import com.ceos.spring_cgv_23rd.domain.screening.adapter.out.persistence.repository.ScreeningJpaRepository;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ScreeningAdapter implements ScreeningPort {

    private final ScreeningJpaRepository screeningJpaRepository;

    @Override
    public Optional<ScreeningInfoResult> findScreeningInfoById(Long screeningId) {
        return screeningJpaRepository.findWithDetailsById(screeningId)
                .map(entity -> new ScreeningInfoResult(
                        entity.getHall().getHallType().getId(),
                        entity.getMovie().getTitle(),
                        entity.getHall().getTheater().getName(),
                        entity.getHall().getName(),
                        entity.getStartAt(),
                        entity.getEndAt(),
                        entity.getPrice()
                ));
    }

    @Override
    public void decreaseScreeningSeats(Long screeningId, int count) {
        ScreeningEntity entity = screeningJpaRepository.findById(screeningId)
                .orElseThrow(() -> new GeneralException(ReservationErrorCode.SCREENING_NOT_FOUND));

        if (entity.getRemainingSeats() < count) {
            throw new GeneralException(ReservationErrorCode.NO_REMAINING_SEATS);
        }

        entity.updateRemainingSeats(entity.getRemainingSeats() - count);
    }

    @Override
    public void increaseScreeningSeats(Long screeningId, int count) {
        ScreeningEntity entity = screeningJpaRepository.findById(screeningId)
                .orElseThrow(() -> new GeneralException(ReservationErrorCode.SCREENING_NOT_FOUND));

        if (entity.getRemainingSeats() + count > entity.getTotalSeats()) {
            throw new GeneralException(ReservationErrorCode.INVALID_SEAT_COUNT);
        }

        entity.updateRemainingSeats(entity.getRemainingSeats() + count);
    }
}
