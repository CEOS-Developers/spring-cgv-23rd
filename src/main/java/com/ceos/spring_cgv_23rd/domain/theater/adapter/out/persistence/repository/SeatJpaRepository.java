package com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {

    @Query("SELECT s FROM SeatEntity s " +
            "WHERE s.id IN :seatIds " +
            "AND s.hallType.id = :hallTypeId")
    List<SeatEntity> findAllByIdInAndHallTypeId(List<Long> seatIds, Long hallTypeId);
}
