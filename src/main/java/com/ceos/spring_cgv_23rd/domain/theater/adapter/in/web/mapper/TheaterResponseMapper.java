package com.ceos.spring_cgv_23rd.domain.theater.adapter.in.web.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ceos.spring_cgv_23rd.domain.theater.adapter.in.web.dto.response.TheaterResponse;
import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.TheaterDetailResult;
import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.TheaterResult;
import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.ToggleTheaterLikeResult;

@Component
public class TheaterResponseMapper {

	public List<TheaterResponse.TheaterListResponse> toTheaterListResponse(List<TheaterResult> results) {
		return results.stream()
			.map(this::toTheaterListDto)
			.toList();
	}

	public TheaterResponse.TheaterDetailResponse toTheaterDetailResponse(TheaterDetailResult result) {
		return TheaterResponse.TheaterDetailResponse.builder()
			.id(result.id())
			.name(result.name())
			.address(result.address())
			.description(result.description())
			.isOpened(result.isOpened())
			.build();
	}

	public TheaterResponse.TheaterLikeResponse toTheaterLikeResponse(ToggleTheaterLikeResult result) {
		return TheaterResponse.TheaterLikeResponse.builder()
			.theaterId(result.theaterId())
			.liked(result.liked())
			.build();
	}

	private TheaterResponse.TheaterListResponse toTheaterListDto(TheaterResult result) {
		return TheaterResponse.TheaterListResponse.builder()
			.id(result.id())
			.name(result.name())
			.address(result.address())
			.isOpened(result.isOpened())
			.build();
	}
}
