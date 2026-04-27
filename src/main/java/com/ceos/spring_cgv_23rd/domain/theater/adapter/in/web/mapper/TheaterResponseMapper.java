package com.ceos.spring_cgv_23rd.domain.theater.adapter.in.web.mapper;

import com.ceos.spring_cgv_23rd.domain.theater.adapter.in.web.dto.response.TheaterResponse;
import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.ToggleTheaterLikeResult;
import com.ceos.spring_cgv_23rd.domain.theater.domain.Theater;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TheaterResponseMapper {

    public List<TheaterResponse.TheaterListResponse> toTheaterListResponse(List<Theater> theaters) {
        return theaters.stream()
                .map(this::toTheaterListDto)
                .toList();
    }

    public TheaterResponse.TheaterDetailResponse toTheaterDetailResponse(Theater theater) {
        return TheaterResponse.TheaterDetailResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .address(theater.getAddress())
                .description(theater.getDescription())
                .isOpened(theater.isOpened())
                .build();
    }

    public TheaterResponse.TheaterLikeResponse toTheaterLikeResponse(ToggleTheaterLikeResult result) {
        return TheaterResponse.TheaterLikeResponse.builder()
                .theaterId(result.theaterId())
                .liked(result.liked())
                .build();
    }

    private TheaterResponse.TheaterListResponse toTheaterListDto(Theater theater) {
        return TheaterResponse.TheaterListResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .address(theater.getAddress())
                .isOpened(theater.isOpened())
                .build();
    }
}
