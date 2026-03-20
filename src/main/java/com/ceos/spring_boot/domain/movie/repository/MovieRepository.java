package com.ceos.spring_boot.domain.movie.repository;

import com.ceos.spring_boot.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    // 개봉일 순으로 전체 조회 (최신순)
    List<Movie> findAllByOrderByReleaseDateDesc();

    // 제목으로 영화 검색
    List<Movie> findByTitleContaining(String title);

}