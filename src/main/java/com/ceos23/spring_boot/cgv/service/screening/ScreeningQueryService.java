package com.ceos23.spring_boot.cgv.service.screening;

import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationStatus;
import com.ceos23.spring_boot.cgv.dto.screening.SeatAvailabilityResponse;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.cinema.SeatTemplateRepository;
import com.ceos23.spring_boot.cgv.repository.movie.ScreeningRepository;
import com.ceos23.spring_boot.cgv.repository.reservation.ReservationSeatRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScreeningQueryService {

    private final ScreeningRepository screeningRepository;
    private final SeatTemplateRepository seatTemplateRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    public List<Screening> getScreenings(Long movieId, Long cinemaId) {
        if (movieId != null && cinemaId != null) {
            return screeningRepository.findAllByMovieIdAndScreenCinemaIdOrderByStartTimeAsc(movieId, cinemaId);
        }

        if (movieId != null) {
            return screeningRepository.findAllByMovieIdOrderByStartTimeAsc(movieId);
        }

        if (cinemaId != null) {
            return screeningRepository.findAllByScreenCinemaIdOrderByStartTimeAsc(cinemaId);
        }

        return screeningRepository.findAllByOrderByStartTimeAsc();
    }

    public SeatAvailabilityResponse getSeatAvailability(Long screeningId) {
        Screening screening = screeningRepository.findByIdWithDetails(screeningId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SCREENING_NOT_FOUND));
        List<SeatTemplate> seatTemplates = seatTemplateRepository
                .findAllBySeatLayoutIdOrderByRowNameAscColNumberAsc(screening.getSeatLayoutId());
        Set<Long> reservedSeatTemplateIds = new HashSet<>(findReservedSeatTemplateIds(screening));

        return SeatAvailabilityResponse.of(screening, seatTemplates, reservedSeatTemplateIds);
    }

    private List<Long> findReservedSeatTemplateIds(Screening screening) {
        return reservationSeatRepository.findActiveSeatTemplateIdsByScreening(
                screening,
                ReservationStatus.CONFIRMED,
                ReservationStatus.PENDING_PAYMENT,
                LocalDateTime.now()
        );
    }
}
