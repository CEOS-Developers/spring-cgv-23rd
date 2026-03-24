package com.ceos.spring_cgv_23rd.domain.screening.dto;

import com.ceos.spring_cgv_23rd.domain.movie.enums.AgeRating;
import com.ceos.spring_cgv_23rd.domain.screening.entity.Screening;
import com.ceos.spring_cgv_23rd.domain.theater.entity.Hall;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScreeningResponseDTO {

    // 영화별 상영 스케줄
    @Builder
    public record ScreeningByMovieResponseDTO(
            Long hallId,
            String hallName,
            String hallTypeName,
            List<ScreeningInfoDTO> screenings
    ) {
        public static List<ScreeningByMovieResponseDTO> from(List<Screening> screenings) {
            Map<Hall, List<Screening>> grouped = screenings.stream()
                    .collect(Collectors.groupingBy(
                            Screening::getHall,
                            Collectors.toList()));

            return grouped.entrySet().stream()
                    .map(entry -> {
                        Hall hall = entry.getKey();

                        return ScreeningByMovieResponseDTO.builder()
                                .hallId(hall.getId())
                                .hallName(hall.getName())
                                .hallTypeName(hall.getHallType().getName())
                                .screenings(entry.getValue().stream()
                                        .map(ScreeningInfoDTO::from)
                                        .toList())
                                .build();
                    })
                    .toList();
        }
    }


    // 극장별 상영 스케줄
    @Builder
    public record ScreeningByTheaterResponseDTO(
            Long movieId,
            String movieTitle,
            String posterUrl,
            AgeRating ageRating,
            List<ScreeningWithHallDTO> screenings
    ) {
        public static List<ScreeningByTheaterResponseDTO> from(List<Screening> screenings) {
            Map<Long, List<Screening>> grouped = screenings.stream()
                    .collect(Collectors.groupingBy(
                            s -> s.getMovie().getId(),
                            Collectors.toList()));

            return grouped.values().stream()
                    .map(screeningList -> {
                        var movie = screeningList.getFirst().getMovie();

                        return ScreeningByTheaterResponseDTO.builder()
                                .movieId(movie.getId())
                                .movieTitle(movie.getTitle())
                                .posterUrl(movie.getPosterUrl())
                                .ageRating(movie.getAgeRating())
                                .screenings(screeningList.stream()
                                        .map(ScreeningWithHallDTO::from)
                                        .toList())
                                .build();
                    })
                    .toList();
        }
    }


    @Builder
    private record ScreeningInfoDTO(
            Long screeningId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer totalSeats,
            Integer remainingSeats
    ) {
        public static ScreeningInfoDTO from(Screening screening) {
            return ScreeningInfoDTO.builder()
                    .screeningId(screening.getId())
                    .startAt(screening.getStartAt())
                    .endAt(screening.getEndAt())
                    .totalSeats(screening.getTotalSeats())
                    .remainingSeats(screening.getRemainingSeats())
                    .build();
        }
    }


    @Builder
    private record ScreeningWithHallDTO(
            Long screeningId,
            String hallName,
            String hallTypeName,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer totalSeats,
            Integer remainingSeats
    ) {
        public static ScreeningWithHallDTO from(Screening screening) {
            Hall hall = screening.getHall();

            return ScreeningWithHallDTO.builder()
                    .screeningId(screening.getId())
                    .hallName(hall.getName())
                    .hallTypeName(hall.getHallType().getName())
                    .startAt(screening.getStartAt())
                    .endAt(screening.getEndAt())
                    .totalSeats(screening.getTotalSeats())
                    .remainingSeats(screening.getRemainingSeats())
                    .build();
        }
    }
}
