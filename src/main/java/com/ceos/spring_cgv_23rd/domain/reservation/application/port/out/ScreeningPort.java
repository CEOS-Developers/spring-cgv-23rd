package com.ceos.spring_cgv_23rd.domain.reservation.application.port.out;

import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ScreeningInfoResult;

import java.util.Optional;

public interface ScreeningPort {

    Optional<ScreeningInfoResult> findScreeningInfoById(Long screeningId);

    void decreaseScreeningSeats(Long screeningId, int count);

    void increaseScreeningSeats(Long screeningId, int count);
}
