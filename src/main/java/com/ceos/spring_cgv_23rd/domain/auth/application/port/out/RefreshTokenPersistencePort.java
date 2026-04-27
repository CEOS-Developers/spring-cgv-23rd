package com.ceos.spring_cgv_23rd.domain.auth.application.port.out;

import com.ceos.spring_cgv_23rd.domain.auth.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenPersistencePort {

    Optional<RefreshToken> findByToken(String token);

    RefreshToken save(RefreshToken refreshToken);

    void delete(RefreshToken refreshToken);

    void deleteByToken(String token);

    void deleteAllByUserId(Long userId);
}
