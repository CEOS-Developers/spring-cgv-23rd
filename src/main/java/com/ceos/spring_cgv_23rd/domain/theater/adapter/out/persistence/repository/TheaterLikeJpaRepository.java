package com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.TheaterLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TheaterLikeJpaRepository extends JpaRepository<TheaterLikeEntity, Long> {

    Optional<TheaterLikeEntity> findByUserIdAndTheaterId(Long userId, Long theaterId);
}
