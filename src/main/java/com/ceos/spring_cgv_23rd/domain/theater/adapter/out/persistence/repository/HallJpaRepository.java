package com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.HallEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HallJpaRepository extends JpaRepository<HallEntity, Long> {

    @Query("SELECT h FROM HallEntity h " +
            "JOIN FETCH h.hallType " +
            "WHERE h.id IN :hallIds")
    List<HallEntity> findAllWithHallTypeByIdIn(List<Long> hallIds);
}
