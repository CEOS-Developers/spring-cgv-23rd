package com.ceos23.spring_boot.domain.theater.repository;

import com.ceos23.spring_boot.domain.theater.entity.ScreenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScreenTypeRepository extends JpaRepository<ScreenType, Long> {
    Optional<ScreenType> findByIdAndDeletedAtIsNull(Long id);
}
