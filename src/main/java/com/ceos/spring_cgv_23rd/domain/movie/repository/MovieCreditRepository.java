package com.ceos.spring_cgv_23rd.domain.movie.repository;

import com.ceos.spring_cgv_23rd.domain.movie.entity.MovieCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieCreditRepository extends JpaRepository<MovieCredit, Long> {

    @Query("SELECT mc FROM MovieCredit mc JOIN FETCH mc.contributor WHERE mc.movie.id = :movieId")
    List<MovieCredit> findByMovieIdWithContributor(Long movieId);
}
