package com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.mapper;

import com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.entity.ReservationEntity;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.Reservation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationPersistenceMapper {

    //  Entity → Domain
    public Reservation toDomain(ReservationEntity entity, List<Long> seatIds) {
        return Reservation.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getGuestId(),
                entity.getScreeningId(),
                entity.getReservationNumber(),
                entity.getPaymentId(),
                entity.getStatus(),
                entity.getTotalPrice(),
                seatIds,
                entity.getCreatedAt()
        );
    }

    // Domain → Entity
    public ReservationEntity toEntity(Reservation domain) {
        if (domain.isGuest()) {
            return ReservationEntity.createForGuest(
                    domain.getGuestId(),
                    domain.getScreeningId(),
                    domain.getReservationNumber(),
                    domain.getPaymentId(),
                    domain.getStatus(),
                    domain.getTotalPrice()
            );
        }
        return ReservationEntity.createForUser(
                domain.getUserId(),
                domain.getScreeningId(),
                domain.getReservationNumber(),
                domain.getPaymentId(),
                domain.getStatus(),
                domain.getTotalPrice()
        );
    }
}
