package com.ceos23.spring_cgv_23rd.Media.Repository;

import com.ceos23.spring_cgv_23rd.Media.Domain.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {
}
