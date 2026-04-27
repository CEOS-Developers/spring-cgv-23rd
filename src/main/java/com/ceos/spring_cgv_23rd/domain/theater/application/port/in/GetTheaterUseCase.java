package com.ceos.spring_cgv_23rd.domain.theater.application.port.in;

import com.ceos.spring_cgv_23rd.domain.theater.domain.Theater;

import java.util.List;

public interface GetTheaterUseCase {

    List<Theater> getTheaters();

    Theater getTheaterDetail(long theaterId);
}
