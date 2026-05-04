package com.ceos.spring_boot.domain.movie.repository;

import com.ceos.spring_boot.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

}