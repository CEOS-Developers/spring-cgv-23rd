package com.ceos.spring_cgv_23rd.domain.screening.adapter.in.web.mapper;

import com.ceos.spring_cgv_23rd.domain.screening.adapter.in.web.dto.response.ScreeningResponse;
import com.ceos.spring_cgv_23rd.domain.screening.application.dto.result.ScreeningDetailResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScreeningResponseMapper {

    // 상영관별 그룹핑후 영화별 상영 일정 응답
    public List<ScreeningResponse.ScreeningByMovieResponse> toMovieResponses(List<ScreeningDetailResult> details) {
        return details.stream()
                .collect(Collectors.groupingBy(ScreeningDetailResult::hallId))
                .values().stream()
                .map(list -> {
                    ScreeningDetailResult first = list.getFirst();

                    List<ScreeningResponse.ScreeningInfo> infos = list.stream()
                            .map(d -> ScreeningResponse.ScreeningInfo.builder()
                                    .screeningId(d.screeningId())
                                    .startAt(d.startAt())
                                    .endAt(d.endAt())
                                    .totalSeats(d.totalSeats())
                                    .remainingSeats(d.remainingSeats())
                                    .build())
                            .toList();

                    return ScreeningResponse.ScreeningByMovieResponse.builder()
                            .hallId(first.hallId())
                            .hallName(first.hallName())
                            .hallTypeName(first.hallTypeName())
                            .screenings(infos)
                            .build();
                })
                .toList();
    }

    // 영화별 그룹핑후 극장별 상영 일정 응답
    public List<ScreeningResponse.ScreeningByTheaterResponse> toTheaterResponses(List<ScreeningDetailResult> details) {
        return details.stream()
                .collect(Collectors.groupingBy(ScreeningDetailResult::movieId))
                .values().stream()
                .map(list -> {
                    ScreeningDetailResult first = list.getFirst();

                    List<ScreeningResponse.ScreeningWithHallInfo> infos = list.stream()
                            .map(d -> ScreeningResponse.ScreeningWithHallInfo.builder()
                                    .screeningId(d.screeningId())
                                    .hallName(d.hallName())
                                    .hallTypeName(d.hallTypeName())
                                    .startAt(d.startAt())
                                    .endAt(d.endAt())
                                    .totalSeats(d.totalSeats())
                                    .remainingSeats(d.remainingSeats())
                                    .build())
                            .toList();

                    return ScreeningResponse.ScreeningByTheaterResponse.builder()
                            .movieId(first.movieId())
                            .movieTitle(first.movieTitle())
                            .posterUrl(first.posterUrl())
                            .ageRating(first.ageRating())
                            .screenings(infos)
                            .build();
                })
                .toList();
    }

}
