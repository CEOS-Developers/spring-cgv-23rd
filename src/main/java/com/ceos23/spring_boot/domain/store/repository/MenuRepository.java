package com.ceos23.spring_boot.domain.store.repository;

import com.ceos23.spring_boot.domain.store.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findByIdAndDeletedAtIsNull(Long id);
    List<Menu> findAllByIdInAndDeletedAtIsNull(List<Long> ids);
}
