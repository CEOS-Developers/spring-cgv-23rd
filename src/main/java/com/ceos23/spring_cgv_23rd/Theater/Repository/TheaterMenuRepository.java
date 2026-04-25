package com.ceos23.spring_cgv_23rd.Theater.Repository;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface TheaterMenuRepository extends JpaRepository<TheaterMenu, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    List<TheaterMenu> findByTheater(Theater theater);
}
