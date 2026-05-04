package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.TheaterItemStock;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TheaterItemStockRepository extends JpaRepository<TheaterItemStock, Long> {

    Optional<TheaterItemStock> findByTheaterIdAndItemId(Long theaterId, Long itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TheaterItemStock> findWithLockByTheaterIdAndItemId(Long theaterId, Long itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select s
            from TheaterItemStock s
            where s.theater.id = :theaterId
            and s.item.id in :itemIds
            order by s.item.id asc
            """)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")
    })
    List<TheaterItemStock> findAllWithLockByTheaterIdAndItemIdsOrderByItemId(
            @Param("theaterId") Long theaterId,
            @Param("itemIds") List<Long> itemIds
    );
}