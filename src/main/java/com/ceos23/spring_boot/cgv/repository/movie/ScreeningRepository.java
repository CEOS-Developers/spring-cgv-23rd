package com.ceos23.spring_boot.cgv.repository.movie;

import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select screening
            from Screening screening
            where screening.id = :screeningId
            """)
    Optional<Screening> findByIdWithPessimisticLock(@Param("screeningId") Long screeningId);
}
