package com.ceos23.spring_cgv_23rd.Screen.Service;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservedSeatQuantityResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationSeatRepository;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreenWrapperDTO;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningWrapperDTO;
import com.ceos23.spring_cgv_23rd.Screen.DTO.ScreeningSearchQueryResultDTO;
import com.ceos23.spring_cgv_23rd.Screen.Domain.CinemaType;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreeningRepository;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ScreeningService {
    private final ReservationSeatRepository reservationSeatRepository;
    private final TheaterRepository theaterRepository;
    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;

    /**
     * 사용자가 영화관id, 관람일자를 요청에 담아 보내면
     * 영화관 별 이용가능한 시간 및 상영관에 대한 정보를 제공합니다.
     *
     * @param theaterId 확인할 영화관id
     * @param date 확인할 날짜
     * @param end
     * @return 상영관에 대한 정보
     */
    @Transactional
    public List<ScreeningSearchResponseDTO> searchMovieWithTheaterId(long theaterId,
                                                                     LocalDateTime date,
                                                                     LocalDateTime end) {

        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_THEATER));

        List<Screening> screenings = screeningRepository.findByTheaterAndDate(theater, date, end);

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
     * 사용자가 영화관id, 관람일자, 영화id를 요청에 담아 전송하면
     * 영화관 별 이용가능한 시간 및 상영관에 대한 정보를 제공합니다.
     *
     * @param theaterId 확인할 영화관id
     * @param movieId 확인할 영화id
     * @param start 확인할 날짜
     * @return 확인된 정보
     */
    public ScreeningSearchResponseDTO searchMovieWithTheaterId(long theaterId,
                                                                                     long movieId,
                                                                                     LocalDateTime start,
                                                               LocalDateTime end){
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_THEATER));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MOVIE));

        System.out.println("서비스 시작 >>> ");

        Map<CinemaType, List<ScreeningSearchQueryResultDTO>> screeningSearchResult = screeningRepository.findByTheaterAndMovie(theater, movie, start, end)
                .stream().collect(
                        Collectors.groupingBy(
                                ScreeningSearchQueryResultDTO::type
                        ));

        System.out.println("grouping >>> " + screeningSearchResult);

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

        System.out.println("res >>> " + ScreeningSearchResponseDTO.create(movie, screenWrapperDTOs));
        return ScreeningSearchResponseDTO.create(movie, screenWrapperDTOs);
    }
}