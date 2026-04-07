package com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.HallEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallJpaRepository extends JpaRepository<HallEntity, Long> {
}
