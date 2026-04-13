package com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence;

import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.SeatInfoResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.SeatPort;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.SeatEntity;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository.SeatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SeatAdapter implements SeatPort {

    private final SeatJpaRepository seatJpaRepository;

    @Override
    public Map<Long, SeatInfoResult> findSeatInfoByIdsAndHallTypeId(List<Long> seatIds, Long hallTypeId) {
        return seatJpaRepository.findAllByIdInAndHallTypeId(seatIds, hallTypeId).stream()
                .collect(Collectors.toMap(
                        SeatEntity::getId,
                        seat -> new SeatInfoResult(
                                seat.getId(),
                                seat.getRowNum(),
                                seat.getColNum())
                ));
    }
}
