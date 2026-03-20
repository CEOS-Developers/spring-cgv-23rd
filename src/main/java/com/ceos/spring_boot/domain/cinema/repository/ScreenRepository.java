package com.ceos.spring_boot.domain.cinema.repository;

import com.ceos.spring_boot.domain.cinema.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen, Long> {

    List<Screen> findByCinemaId(Long cinemaId);

}
