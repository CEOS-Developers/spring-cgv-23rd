package com.ceos.spring_cgv_23rd.domain.reservation.application.port.out;

import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.SeatInfoResult;

import java.util.List;
import java.util.Map;

public interface SeatPort {

    Map<Long, SeatInfoResult> findSeatInfoByIdsAndHallTypeId(List<Long> seatIds, Long hallTypeId);
}
