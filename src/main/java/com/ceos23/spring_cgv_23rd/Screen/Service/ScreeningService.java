package com.ceos23.spring_cgv_23rd.Screen.Service;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationRepository;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreenWrapperDTO;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningWrapperDTO;
import com.ceos23.spring_cgv_23rd.Screen.Domain.CinemaType;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screen;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreenRepository;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreeningRepository;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import jakarta.persistence.EntityNotFoundException;
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
public class ScreeningService {
    TheaterRepository theaterRepository;
    ScreenRepository screenRepository;
    ScreeningRepository screeningRepository;
    ReservationRepository reservationRepository;
    MovieRepository movieRepository;

    public ScreeningService(TheaterRepository theaterRepository,
                            ScreenRepository screenRepository,
                            ScreeningRepository screeningRepository,
                            ReservationRepository reservationRepository,
                            MovieRepository movieRepository){
        this.theaterRepository = theaterRepository;
        this.screenRepository = screenRepository;
        this.screeningRepository = screeningRepository;
        this.reservationRepository = reservationRepository;
        this.movieRepository = movieRepository;
    }

    /**
     * 사용자가 영화관 id와 관림일자를 건네주면 영화관별로 이용가능한 시간 및 상영관에 대한 정보를 제공합니다.
     * TODO: 관람일자 설정하기: 완료
     */
    public ResponseEntity<List<ScreeningSearchResponseDTO>> searchMovieWithTheaterId(long theaterId,
                                                                                     LocalDate date){
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 영화관이 존재하지 않습니다."));

        List<Screening> screenings = screeningRepository.findByScreen_Theater(theater).stream()
                .filter(s -> s.getStartTime().isAfter(LocalDateTime.now()))
                .filter(s -> s.getStartTime().toLocalDate().isEqual(date))
                .toList();

        Map<Movie, Map<CinemaType, List<Screening>>> gr =
                screenings.stream()
                        .collect(Collectors.groupingBy(
                                Screening::getMovie,
                                Collectors.groupingBy(s -> s.getScreen().getCinemaType())
                        ));

        List<ScreeningSearchResponseDTO> res = new ArrayList<>();

        for (Movie movie : gr.keySet()){
            List<ScreenWrapperDTO> screenWrapperDTOS = new ArrayList<>();
            Map<CinemaType, List<Screening>> screeningWithCinemaType = gr.get(movie);

            for (CinemaType type : screeningWithCinemaType.keySet()){

                List<ScreeningWrapperDTO> screeningWrapperDTOs =
                        screeningWithCinemaType.get(type).stream()
                                .map(s -> ScreeningWrapperDTO.create(
                                        s.getScreen(),
                                        s,
                                        movie,
                                        s.getScreen().getSeatAmount() -
                                                reservationRepository.findByScreening(s).stream()
                                                                .mapToInt(r -> r.getReservationSeats().size())
                                                                .sum(),
                                        s.getScreen().getSeatAmount()
                                ))
                                .toList();

                 screenWrapperDTOS.add(ScreenWrapperDTO.create(type, screeningWrapperDTOs));
            }
            res.add(ScreeningSearchResponseDTO.create(movie, screenWrapperDTOS));
        }

        return ResponseEntity.ok(res);
    }

    /**
     * 사용자가 영화관 id와 관림일자, 영화id를 건네주면 영화관별로 이용가능한 시간 및 상영관에 대한 정보를 제공합니다.
     */
    public ResponseEntity<List<ScreeningSearchResponseDTO>> searchMovieWithTheaterId(long theaterId,
                                                                                     long movieId,
                                                                                     LocalDate date){
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 영화관이 존재하지 않습니다."));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 영화가 존재하지 않습니다."));

        List<Screening> screenings = screeningRepository.findByScreen_TheaterAndMovie(theater, movie).stream()
                .filter(s -> s.getStartTime().isAfter(LocalDateTime.now()))
                .filter(s -> s.getStartTime().toLocalDate().isEqual(date))
                .toList();

        Map<CinemaType, List<Screening>> gr =
                screenings.stream()
                        .collect(Collectors.groupingBy(
                                s -> s.getScreen().getCinemaType()
                        ));


        List<ScreeningSearchResponseDTO> res = new ArrayList<>();
        List<ScreenWrapperDTO> wrd = new ArrayList<>();

        for (CinemaType type : gr.keySet()){
            List<ScreeningWrapperDTO> screeningWrapperDTOs =
                    gr.get(type).stream()
                            .map(s -> ScreeningWrapperDTO.create(
                                    s.getScreen(),
                                    s,
                                    movie,
                                    s.getScreen().getSeatAmount() -
                                            reservationRepository.findByScreening(s).stream()
                                                    .mapToInt(r -> r.getReservationSeats().size())
                                                    .sum(),
                                    s.getScreen().getSeatAmount()
                            ))
                            .toList();
            wrd.add(ScreenWrapperDTO.create(type, screeningWrapperDTOs));
        }

        res.add(ScreeningSearchResponseDTO.create(movie, wrd));
        return ResponseEntity.ok(res);
    }
}