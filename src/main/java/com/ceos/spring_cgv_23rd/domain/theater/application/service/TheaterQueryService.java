package com.ceos.spring_cgv_23rd.domain.theater.application.service;

import com.ceos.spring_cgv_23rd.domain.theater.application.port.in.GetTheaterUseCase;
import com.ceos.spring_cgv_23rd.domain.theater.application.port.out.TheaterPersistencePort;
import com.ceos.spring_cgv_23rd.domain.theater.domain.Theater;
import com.ceos.spring_cgv_23rd.domain.theater.exception.TheaterErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheaterQueryService implements GetTheaterUseCase {

    private final TheaterPersistencePort theaterPersistencePort;

    @Override
    public List<Theater> getTheaters() {
        return theaterPersistencePort.findAllTheaters();
    }

    @Override
    public Theater getTheaterDetail(long theaterId) {
        return theaterPersistencePort.findTheaterById(theaterId)
                .orElseThrow(() -> new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND));
    }

}
