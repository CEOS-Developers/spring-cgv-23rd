package com.ceos.spring_cgv_23rd.domain.theater.application.service;

import com.ceos.spring_cgv_23rd.domain.theater.application.dto.command.ToggleTheaterLikeCommand;
import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.ToggleTheaterLikeResult;
import com.ceos.spring_cgv_23rd.domain.theater.application.port.in.ToggleTheaterLikeUseCase;
import com.ceos.spring_cgv_23rd.domain.theater.application.port.out.TheaterPersistencePort;
import com.ceos.spring_cgv_23rd.domain.theater.domain.TheaterLike;
import com.ceos.spring_cgv_23rd.domain.theater.exception.TheaterErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TheaterCommandService implements ToggleTheaterLikeUseCase {

    private final TheaterPersistencePort theaterPersistencePort;

    @Override
    @Transactional
    public ToggleTheaterLikeResult execute(ToggleTheaterLikeCommand command) {

        // 영화관 조회
        if (!theaterPersistencePort.existsTheaterById(command.theaterId())) {
            throw new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND);
        }

        // 영화관 찜 여부 조회
        Optional<TheaterLike> existingLike = theaterPersistencePort
                .findTheaterLikeByUserAndTheater(command.userId(), command.theaterId());

        boolean liked;
        if (existingLike.isPresent()) {
            // 찜 취소
            theaterPersistencePort.deleteTheaterLike(existingLike.get());
            liked = false;
        } else {
            // 찜 등록
            TheaterLike theaterLike = TheaterLike.create(command.userId(), command.theaterId());
            theaterPersistencePort.saveTheaterLike(theaterLike);
            liked = true;
        }

        return new ToggleTheaterLikeResult(command.theaterId(), liked);
    }
}
