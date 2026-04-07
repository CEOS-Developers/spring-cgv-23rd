package com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {

    List<SeatEntity> findAllByIdIn(List<Long> seatIds);
}
