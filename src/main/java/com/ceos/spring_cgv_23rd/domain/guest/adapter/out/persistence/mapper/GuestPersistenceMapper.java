package com.ceos.spring_cgv_23rd.domain.guest.adapter.out.persistence.mapper;

import com.ceos.spring_cgv_23rd.domain.guest.adapter.out.persistence.entity.GuestEntity;
import com.ceos.spring_cgv_23rd.domain.guest.domain.Guest;
import org.springframework.stereotype.Component;

@Component
public class GuestPersistenceMapper {

    //  Entity → Domain
    public Guest toDomain(GuestEntity entity) {
        return Guest.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .birth(entity.getBirth())
                .password(entity.getPassword())
                .build();
    }


    // Domain → Entity
    public GuestEntity toEntity(Guest domain) {
        return GuestEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .phone(domain.getPhone())
                .birth(domain.getBirth())
                .password(domain.getPassword())
                .build();
    }
}
