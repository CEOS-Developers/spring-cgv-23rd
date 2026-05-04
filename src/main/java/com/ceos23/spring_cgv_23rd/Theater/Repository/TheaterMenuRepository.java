package com.ceos23.spring_cgv_23rd.Theater.Repository;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TheaterMenuRepository extends JpaRepository<TheaterMenu, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    List<TheaterMenu> findByTheater(Theater theater);

    @Query("""
        select t from TheaterMenu t
        where t.id = :id
    """)
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<TheaterMenu> findByIdForUpdate(@Param("id") long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update TheaterMenu m
        set m.sold = m.sold - :qty
        where m.id = :id
        and m.sold >= :qty
    """)
    int decreaseStock(@Param("id") long id,
                      @Param("qty") int qty);

    @Modifying
    @Query("""
        update TheaterMenu m
        set m.sold = m.sold + :qty
        where m.id = :id
    """)
    int increaseStock(@Param("id") long id,
                      @Param("qty") int qty);
}
