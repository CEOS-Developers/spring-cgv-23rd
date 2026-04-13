package com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.mapper;

import com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.entity.ReservationEntity;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.Reservation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationPersistenceMapper {

    //  Entity → Domain
    public Reservation toDomain(ReservationEntity entity, List<Long> seatIds) {
        return Reservation.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .guestId(entity.getGuestId())
                .screeningId(entity.getScreeningId())
                .reservationNumber(entity.getReservationNumber())
                .status(entity.getStatus())
                .totalPrice(entity.getTotalPrice())
                .createdAt(entity.getCreatedAt())
                .seatIds(seatIds)
                .build();
    }

    // Domain → Entity
    public ReservationEntity toEntity(Reservation domain) {
        if (domain.isGuest()) {
            return ReservationEntity.createForGuest(
                    domain.getGuestId(),
                    domain.getScreeningId(),
                    domain.getReservationNumber(),
                    domain.getStatus(),
                    domain.getTotalPrice()
            );
        }
        return ReservationEntity.createForUser(
                domain.getUserId(),
                domain.getScreeningId(),
                domain.getReservationNumber(),
                domain.getStatus(),
                domain.getTotalPrice()
        );
    }
}
