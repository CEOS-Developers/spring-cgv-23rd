package com.ceos.spring_cgv_23rd.domain.screening.adapter.out.persistence.mapper;

import com.ceos.spring_cgv_23rd.domain.screening.adapter.out.persistence.entity.ScreeningEntity;
import com.ceos.spring_cgv_23rd.domain.screening.domain.Screening;
import org.springframework.stereotype.Component;

@Component
public class ScreeningPersistenceMapper {

    //  Entity → Domain
    public Screening toDomain(ScreeningEntity entity) {
        return Screening.builder()
                .id(entity.getId())
                .movieId(entity.getMovie().getId())
                .hallId(entity.getHall().getId())
                .startAt(entity.getStartAt())
                .endAt(entity.getEndAt())
                .totalSeats(entity.getTotalSeats())
                .remainingSeats(entity.getRemainingSeats())
                .price(entity.getPrice())
                .build();
    }
}
