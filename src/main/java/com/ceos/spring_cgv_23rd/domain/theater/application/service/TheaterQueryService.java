package com.ceos.spring_cgv_23rd.domain.theater.application.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.TheaterDetailResult;
import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.TheaterResult;
import com.ceos.spring_cgv_23rd.domain.theater.application.port.in.GetTheaterUseCase;
import com.ceos.spring_cgv_23rd.domain.theater.application.port.out.TheaterPersistencePort;
import com.ceos.spring_cgv_23rd.domain.theater.exception.TheaterErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheaterQueryService implements GetTheaterUseCase {

	private final TheaterPersistencePort theaterPersistencePort;

	@Override
	@Cacheable(value = "theater:list", key = "'all'")
	public List<TheaterResult> getTheaters() {
		return theaterPersistencePort.findAllTheaters()
			.stream()
			.map(TheaterResult::from)
			.toList();
	}

	@Override
	@Cacheable(value = "theater:detail", key = "#theaterId")
	public TheaterDetailResult getTheaterDetail(long theaterId) {
		return theaterPersistencePort.findTheaterById(theaterId)
			.map(TheaterDetailResult::from)
			.orElseThrow(() -> new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND));
	}

}
