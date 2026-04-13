package com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence;

import com.ceos.spring_cgv_23rd.domain.guest.adapter.out.persistence.entity.GuestEntity;
import com.ceos.spring_cgv_23rd.domain.guest.adapter.out.persistence.repository.GuestJpaRepository;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.GuestInfoResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.GuestPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GuestAdapter implements GuestPort {

    private final GuestJpaRepository guestJpaRepository;

    @Override
    public Long saveGuest(String name, String phone, LocalDate birth, String encodedPassword) {
        GuestEntity guest = GuestEntity.builder()
                .name(name)
                .phone(phone)
                .birth(birth)
                .password(encodedPassword)
                .build();

        return guestJpaRepository.save(guest).getId();
    }

    @Override
    public Optional<GuestInfoResult> findGuestInfoById(Long guestId) {
        return guestJpaRepository.findById(guestId)
                .map(entity -> new GuestInfoResult(
                        entity.getId(),
                        entity.getPhone(),
                        entity.getBirth(),
                        entity.getPassword()
                ));
    }
}
