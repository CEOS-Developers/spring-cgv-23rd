package com.ceos.spring_cgv_23rd.domain.movie.repository;

import com.ceos.spring_cgv_23rd.domain.movie.entity.MovieMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieMediaRepository extends JpaRepository<MovieMedia, Long> {

    List<MovieMedia> findByMovieId(Long movieId);
}
