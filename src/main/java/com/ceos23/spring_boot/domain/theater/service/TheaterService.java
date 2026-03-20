package com.ceos23.spring_boot.domain.theater.service;

import com.ceos23.spring_boot.domain.theater.dto.TheaterCreateCommand;
import com.ceos23.spring_boot.domain.theater.dto.TheaterInfo;
import com.ceos23.spring_boot.domain.theater.dto.TheaterSearchCommand;
import com.ceos23.spring_boot.domain.theater.dto.TheaterUpdateCommand;
import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.domain.theater.repository.TheaterRepository;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheaterService {
    private final TheaterRepository theaterRepository;

    public List<TheaterInfo> findTheaters(TheaterSearchCommand command) {
        List<Theater> theaters;

        if (StringUtils.hasText(command.location()))
            theaters = theaterRepository.findByLocation(command.location());
        else
            theaters = theaterRepository.findAll();

        return theaters
                .stream()
                .map(TheaterInfo::from)
                .toList();
    }

    public TheaterInfo findTheater(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.THEATER_NOT_FOUND));

        return TheaterInfo.from(theater);
    }

    @Transactional
    public TheaterInfo createTheater(TheaterCreateCommand command) {
        if (theaterRepository.existsByName(command.name()))
            throw new BusinessException(ErrorCode.DUPLICATE_THEATER_NAME);

        Theater theater = Theater.builder()
                .name(command.name())
                .location(command.location())
                .build();

        theaterRepository.save(theater);

        return TheaterInfo.from(theater);
    }

    @Transactional
    public TheaterInfo updateTheater(Long id, TheaterUpdateCommand command) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(()-> new BusinessException(ErrorCode.THEATER_NOT_FOUND));

        if (!theater.getName().equals(command.name()) && theaterRepository.existsByName(command.name()))
            throw new BusinessException(ErrorCode.DUPLICATE_THEATER_NAME);

        theater.update(command.name(), command.location());

        return TheaterInfo.from(theater);
    }

    @Transactional
    public void deleteTheater(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(()-> new BusinessException(ErrorCode.THEATER_NOT_FOUND));

        theaterRepository.delete(theater);
    }
}
