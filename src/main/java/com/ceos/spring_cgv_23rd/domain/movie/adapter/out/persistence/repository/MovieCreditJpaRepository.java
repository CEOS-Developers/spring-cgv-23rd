package com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity.MovieCreditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieCreditJpaRepository extends JpaRepository<MovieCreditEntity, Long> {

    @Query("SELECT mc FROM MovieCreditEntity mc JOIN FETCH mc.contributor WHERE mc.movie.id = :movieId")
    List<MovieCreditEntity> findByMovieIdWithContributor(Long movieId);
}
