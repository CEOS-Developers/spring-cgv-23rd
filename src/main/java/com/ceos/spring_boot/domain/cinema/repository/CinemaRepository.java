package com.ceos.spring_boot.domain.cinema.repository;

import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {

    List<Cinema> findByRegion(String region); // 지역별 영화관 조회

}