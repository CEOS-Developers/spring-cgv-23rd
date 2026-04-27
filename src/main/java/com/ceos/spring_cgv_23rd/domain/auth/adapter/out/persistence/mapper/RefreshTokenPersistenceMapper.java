package com.ceos.spring_cgv_23rd.domain.auth.adapter.out.persistence.mapper;

import com.ceos.spring_cgv_23rd.domain.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import com.ceos.spring_cgv_23rd.domain.auth.domain.RefreshToken;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenPersistenceMapper {

    //  Entity → Domain
    public RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .expiryDate(entity.getExpiryDate())
                .build();
    }

    // Domain → Entity
    public RefreshTokenEntity toEntity(RefreshToken domain) {
        return RefreshTokenEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .token(domain.getToken())
                .expiryDate(domain.getExpiryDate())
                .build();

    }
}
