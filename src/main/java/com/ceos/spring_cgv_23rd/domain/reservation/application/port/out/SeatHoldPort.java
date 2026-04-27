package com.ceos.spring_cgv_23rd.domain.reservation.application.port.out;

import java.util.List;

public interface SeatHoldPort {

    boolean holdSeats(Long screeningId, List<Long> seatIds, String holderKey, long ttlSeconds);

    boolean isHeldByUser(Long screeningId, List<Long> seatIds, String holderKey);

    void releaseSeats(Long screeningId, List<Long> seatIds);
}
