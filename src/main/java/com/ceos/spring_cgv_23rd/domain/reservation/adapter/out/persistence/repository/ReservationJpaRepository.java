package com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {

}
