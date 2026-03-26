package com.ceos23.spring_boot.cgv.repository.like;
import com.ceos23.spring_boot.cgv.domain.like.CinemaLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CinemaLikeRepository extends JpaRepository<CinemaLike, Long> {

    boolean existsByUserIdAndCinemaId(Long userId, Long cinemaId);

    Optional<CinemaLike> findByUserIdAndCinemaId(Long userId, Long cinemaId);
}