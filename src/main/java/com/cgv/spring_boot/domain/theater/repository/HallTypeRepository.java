package com.cgv.spring_boot.domain.theater.repository;

import com.cgv.spring_boot.domain.theater.entity.HallType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallTypeRepository extends JpaRepository<HallType, Long> {
}
