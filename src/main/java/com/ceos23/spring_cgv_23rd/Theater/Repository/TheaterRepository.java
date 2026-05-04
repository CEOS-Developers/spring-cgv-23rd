package com.ceos23.spring_cgv_23rd.Theater.Repository;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Region;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
    Optional<Theater> findByName(String name);

    List<Theater> findByRegion(Region region);

    List<Theater> findByNameContaining(String name);

    List<Theater> findAllByRegion(Region region);
}
