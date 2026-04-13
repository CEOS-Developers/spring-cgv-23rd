package com.ceos.spring_cgv_23rd.domain.reservation.application.port.out;

import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.GuestInfoResult;

import java.time.LocalDate;
import java.util.Optional;

public interface GuestPort {

    Long saveGuest(String name, String phone, LocalDate birth, String encodedPassword);

    Optional<GuestInfoResult> findGuestInfoById(Long guestId);
}
