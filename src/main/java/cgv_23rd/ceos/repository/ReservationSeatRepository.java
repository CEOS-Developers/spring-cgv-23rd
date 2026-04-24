package cgv_23rd.ceos.repository;

import cgv_23rd.ceos.entity.enums.ReservationStatus;
import cgv_23rd.ceos.entity.reservation.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat,Long> {
    boolean existsByMovieScreenIdAndSeatIdAndReservation_StatusIn(Long movieScreenId, Long seatId, List<ReservationStatus> status);
}
