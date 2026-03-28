package com.ceos.spring_cgv_23rd.domain.guest.repository;

import com.ceos.spring_cgv_23rd.domain.guest.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
