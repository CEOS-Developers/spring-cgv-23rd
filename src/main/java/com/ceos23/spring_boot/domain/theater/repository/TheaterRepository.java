package com.ceos23.spring_boot.domain.theater.repository;

import com.ceos23.spring_boot.domain.theater.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
    Optional<Theater> findByIdAndDeletedAtIsNull(Long id);
    List<Theater> findAllByDeletedAtIsNull();

    List<Theater> findByLocationAndDeletedAtIsNull(String location);
    Boolean existsByNameAndDeletedAtIsNull(String name);
    Optional<Theater> findByNameAndDeletedAtIsNotNull(String name);
}
