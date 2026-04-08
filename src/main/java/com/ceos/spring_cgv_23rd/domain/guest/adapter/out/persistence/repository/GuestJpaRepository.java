package com.ceos.spring_cgv_23rd.domain.guest.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.guest.adapter.out.persistence.entity.GuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestJpaRepository extends JpaRepository<GuestEntity, Long> {
}
