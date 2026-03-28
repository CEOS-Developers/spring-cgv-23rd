package com.ceos23.cgv_clone.global.jwt.repository;

import com.ceos23.cgv_clone.global.jwt.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(Long userId);
}
