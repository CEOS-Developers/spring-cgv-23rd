package com.ceos23.spring_cgv_23rd.Screen.Service;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationType;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservationResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservedSeatQuantityResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationRepository;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationSeatRepository;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreenWrapperDTO;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningWrapperDTO;
import com.ceos23.spring_cgv_23rd.Screen.DTO.ScreeningSearchQueryResultDTO;
import com.ceos23.spring_cgv_23rd.Screen.Domain.CinemaType;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screen;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreenRepository;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreeningRepository;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ScreeningService {
    private final ReservationSeatRepository reservationSeatRepository;
    TheaterRepository theaterRepository;
    ScreeningRepository screeningRepository;
    MovieRepository movieRepository;

    /**
     * 사용자가 영화관 id와 관림일자를 건네주면 영화관별로 이용가능한 시간 및 상영관에 대한 정보를 제공합니다.
     */
    public List<ScreeningSearchResponseDTO> searchMovieWithTheaterId(long theaterId, LocalDate date) {

        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_THEATER));

        List<Screening> screenings = screeningRepository.findByTheater(theater, date);

        Map<Movie, Map<CinemaType, List<Screening>>> grouped =
                screenings.stream()
                        .collect(Collectors.groupingBy(
                                Screening::getMovie,
                                Collectors.groupingBy(s -> s.getScreen().getCinemaType())
                        ));

        List<Long> screeningIds = screenings.stream()
                .map(Screening::getId)
                .toList();

        List<ReservedSeatQuantityResponseDTO> quantityInfos =
                reservationSeatRepository.getQuantityOfReservedSeatByScreening(
                        screeningIds,
                        ReservationStatus.RESERVED
                );

        Map<Long, Long> quantityMap = quantityInfos.stream()
                .collect(Collectors.toMap(
                        ReservedSeatQuantityResponseDTO::screeningId,
                        ReservedSeatQuantityResponseDTO::quantity
                ));

        List<ScreeningSearchResponseDTO> result = new ArrayList<>();

        for (Movie movie : grouped.keySet()) {
            Map<CinemaType, List<Screening>> byCinema = grouped.get(movie);
            List<ScreenWrapperDTO> screenWrapperDTOS = new ArrayList<>();

            for (CinemaType type : CinemaType.values()) {
                List<Screening> list =
                        byCinema.getOrDefault(type, List.of());

                List<ScreeningWrapperDTO> screeningWrapperDTOS =
                        list.stream()
                                .map(s -> {
                                    long reserved = quantityMap.getOrDefault(s.getId(), 0L);

                                    return ScreeningWrapperDTO.create(
                                            s.getScreen(),
                                            s,
                                            movie,
                                            (int) (s.getScreen().getSeatAmount() - reserved),
                                            s.getScreen().getSeatAmount()
                                    );
                                })
                                .toList();

                screenWrapperDTOS.add(ScreenWrapperDTO.create(type, screeningWrapperDTOS));
            }
            result.add(ScreeningSearchResponseDTO.create(movie, screenWrapperDTOS));
        }
        return result;
    }

    /**
     * 사용자가 영화관 id와 관림일자, 영화id를 건네주면 영화관별로 이용가능한 시간 및 상영관에 대한 정보를 제공합니다.
     */
    public ScreeningSearchResponseDTO searchMovieWithTheaterId(long theaterId,
                                                                                     long movieId,
                                                                                     LocalDate date){
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_THEATER));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MOVIE));

        Map<CinemaType, List<ScreeningSearchQueryResultDTO>> screeningSearchResult = screeningRepository.findByTheaterAndMovie(theater, movie, LocalDateTime.now())
                .stream().collect(
                        Collectors.groupingBy(
                                ScreeningSearchQueryResultDTO::type
                        ));

        List<Long> screeningIds = screeningSearchResult.values().stream()
                .flatMap(List::stream)
                .map(ScreeningSearchQueryResultDTO::screeningId)
                .toList();

        List<ReservedSeatQuantityResponseDTO> quantityInfos = reservationSeatRepository.getQuantityOfReservedSeatByScreening(
                screeningIds, ReservationStatus.RESERVED
        );

        Map<Long, Long> quantityMap = quantityInfos.stream()
                .collect(Collectors.toMap(
                        ReservedSeatQuantityResponseDTO::screeningId,
                        ReservedSeatQuantityResponseDTO::quantity
                ));

        List<ScreenWrapperDTO> screenWrapperDTOs = new ArrayList<>();

        for (CinemaType type : CinemaType.values()) {

            List<ScreeningSearchQueryResultDTO> dtos =
                    screeningSearchResult.getOrDefault(type, List.of());

            List<ScreeningWrapperDTO> wrappers = dtos.stream()
                    .map(dto -> {
                        long reserved = quantityMap.getOrDefault(dto.screeningId(), 0L);
                        return ScreeningWrapperDTO.create(dto, (int) reserved);
                    })
                    .toList();

            screenWrapperDTOs.add(
                    ScreenWrapperDTO.create(type, wrappers)
            );
        }

        return ScreeningSearchResponseDTO.create(movie, screenWrapperDTOs);
    }
}