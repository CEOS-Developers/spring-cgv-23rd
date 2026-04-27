package com.ceos.spring_cgv_23rd.domain.auth.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByToken(String token);

    void deleteAllByUserId(Long userId);
}
