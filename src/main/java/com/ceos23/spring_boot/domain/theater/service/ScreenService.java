package com.ceos23.spring_boot.domain.theater.service;

import com.ceos23.spring_boot.domain.theater.dto.ScreenCreateCommand;
import com.ceos23.spring_boot.domain.theater.dto.ScreenInfo;
import com.ceos23.spring_boot.domain.theater.entity.Screen;
import com.ceos23.spring_boot.domain.theater.entity.ScreenType;
import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.domain.theater.repository.*;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreenService {

    private final TheaterRepository theaterRepository;
    private final ScreenTypeRepository screenTypeRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public ScreenInfo createScreenWithSeats(ScreenCreateCommand command) {

        Theater theater = theaterRepository.findByIdAndDeletedAtIsNull(command.theaterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.THEATER_NOT_FOUND));

        ScreenType screenType = screenTypeRepository.findByIdAndDeletedAtIsNull(command.screenTypeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCREEN_TYPE_NOT_FOUND));

        Screen newScreen = Screen.builder()
                .theater(theater)
                .screenType(screenType)
                .name(command.screenName())
                .build();
        screenRepository.save(newScreen);

        int insertedCount = seatRepository.bulkInsertFromTemplate(newScreen.getId(), screenType.getId());

        if (insertedCount == 0) {
            throw new BusinessException(ErrorCode.SEAT_TEMPLATE_NOT_FOUND);
        }

        return ScreenInfo.from(newScreen);
    }
}