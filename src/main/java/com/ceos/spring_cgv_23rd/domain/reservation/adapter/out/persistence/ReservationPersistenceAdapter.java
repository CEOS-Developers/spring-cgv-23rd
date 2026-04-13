package com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence;

import com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.entity.ReservationEntity;
import com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.entity.ReservationSeatEntity;
import com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.mapper.ReservationPersistenceMapper;
import com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.repository.ReservationJpaRepository;
import com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.repository.ReservationSeatJpaRepository;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.ReservationPersistencePort;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.Reservation;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.ReservationStatus;
import com.ceos.spring_cgv_23rd.domain.reservation.exception.ReservationErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservationPersistenceAdapter implements ReservationPersistencePort {

    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationSeatJpaRepository reservationSeatJpaRepository;
    private final ReservationPersistenceMapper mapper;

    @Override
    public Reservation saveReservation(Reservation reservation) {
        ReservationEntity entity = mapper.toEntity(reservation);
        ReservationEntity savedEntity = reservationJpaRepository.save(entity);

        List<ReservationSeatEntity> seatEntities = reservation.getSeatIds().stream()
                .map(seatId -> ReservationSeatEntity.builder()
                        .reservation(savedEntity)
                        .seatId(seatId)
                        .build())
                .toList();
        reservationSeatJpaRepository.saveAll(seatEntities);

        return mapper.toDomain(savedEntity, reservation.getSeatIds());
    }

    @Override
    public Optional<Reservation> findReservationWithSeatsById(Long reservationId) {
        return reservationJpaRepository.findById(reservationId)
                .map(entity -> {
                    List<Long> seatIds = reservationSeatJpaRepository.findByReservationId(entity.getId()).stream()
                            .map(ReservationSeatEntity::getSeatId)
                            .toList();

                    return mapper.toDomain(entity, seatIds);
                });
    }

    @Override
    public void updateReservationStatus(Long reservationId, ReservationStatus status) {
        ReservationEntity entity = reservationJpaRepository.findById(reservationId)
                .orElseThrow(() -> new GeneralException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        entity.updateStatus(status);
    }

    @Override
    public List<Long> findReservedSeatIdsByScreeningId(Long screeningId) {
        return reservationSeatJpaRepository.findReservedSeatIdsByScreeningId(screeningId);
    }
}
