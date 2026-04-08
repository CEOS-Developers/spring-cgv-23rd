package com.ceos.spring_cgv_23rd.domain.guest.application.port.out;

import com.ceos.spring_cgv_23rd.domain.guest.domain.Guest;

import java.util.Optional;

public interface GuestPersistencePort {
    
    Optional<Guest> findById(Long guestId);

    Guest save(Guest guest);
}
