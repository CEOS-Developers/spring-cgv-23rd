package com.ceos.spring_cgv_23rd.domain.auth.adapter.out.persistence;

import com.ceos.spring_cgv_23rd.domain.auth.adapter.out.persistence.mapper.RefreshTokenPersistenceMapper;
import com.ceos.spring_cgv_23rd.domain.auth.adapter.out.persistence.repository.RefreshTokenJpaRepository;
import com.ceos.spring_cgv_23rd.domain.auth.application.port.out.RefreshTokenPersistencePort;
import com.ceos.spring_cgv_23rd.domain.auth.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenPersistencePort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final RefreshTokenPersistenceMapper mapper;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenJpaRepository.findByToken(token)
                .map(mapper::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return mapper.toDomain(refreshTokenJpaRepository.save(mapper.toEntity(refreshToken)));
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        refreshTokenJpaRepository.deleteById(refreshToken.getId());
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenJpaRepository.deleteByToken(token);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        refreshTokenJpaRepository.deleteAllByUserId(userId);
    }

}
