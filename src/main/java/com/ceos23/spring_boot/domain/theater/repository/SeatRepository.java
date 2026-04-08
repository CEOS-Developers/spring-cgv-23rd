package com.ceos23.spring_boot.domain.theater.repository;

import com.ceos23.spring_boot.domain.theater.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "seatGrade")
    @Query("select s from Seat s where s.id in :seatIds AND s.screen.id = :screenId AND s.deletedAt IS NULL")
    List<Seat> findAllByIdAndScreenIdAndDeletedAtIsNullWithLock(
            @Param("seatIds") List<Long> seatIds,
            @Param("screenId") Long screenId
    );

    List<Seat> findAllByIdInAndScreenIdAndDeletedAtIsNull(List<Long> seatIds, Long screenId);

    @Modifying(clearAutomatically = true)
    @Query(value =
            "INSERT INTO seat (screen_id, seat_grade_id, row_name, col_number, deleted_at) " +
                    "SELECT :screenId, st.seat_grade_id, st.row_name, st.col_number, NULL " +
                    "FROM seat_template st " +
                    "WHERE st.screen_type_id = :screenTypeId AND st.deleted_at IS NULL",
            nativeQuery = true)
    int bulkInsertFromTemplate(@Param("screenId") Long screenId, @Param("screenTypeId") Long screenTypeId);
}
