package com.ceos.spring_cgv_23rd.domain.guest.adapter.out.persistence;

import com.ceos.spring_cgv_23rd.domain.guest.adapter.out.persistence.mapper.GuestPersistenceMapper;
import com.ceos.spring_cgv_23rd.domain.guest.adapter.out.persistence.repository.GuestJpaRepository;
import com.ceos.spring_cgv_23rd.domain.guest.application.port.out.GuestPersistencePort;
import com.ceos.spring_cgv_23rd.domain.guest.domain.Guest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GuestPersistenceAdapter implements GuestPersistencePort {

    private final GuestJpaRepository guestJpaRepository;
    private final GuestPersistenceMapper mapper;

    @Override
    public Optional<Guest> findById(Long guestId) {
        return guestJpaRepository.findById(guestId)
                .map(mapper::toDomain);
    }

    @Override
    public Guest save(Guest guest) {
        return mapper.toDomain(guestJpaRepository.save(mapper.toEntity(guest)));
    }
}
