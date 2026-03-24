package com.ceos23.cgv.domain.person.repository;

import com.ceos23.cgv.domain.person.entity.WorkParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkParticipationRepository extends JpaRepository<WorkParticipation, Long> {
    // 특정 영화에 참여한 인물(감독/배우) 목록 조회
    List<WorkParticipation> findByMovieId(Long movieId);

    // 특정 인물(배우/감독)이 참여한 작품 목록
    List<WorkParticipation> findByPersonId(Long personId);
}