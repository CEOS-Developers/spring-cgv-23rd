package com.ceos.spring_cgv_23rd.domain.theater.application.port.in;

import java.util.List;

import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.TheaterDetailResult;
import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.TheaterResult;

public interface GetTheaterUseCase {

	List<TheaterResult> getTheaters();

	TheaterDetailResult getTheaterDetail(long theaterId);
}
