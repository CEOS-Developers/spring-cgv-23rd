package com.ceos23.spring_boot.domain.theater.service;

import com.ceos23.spring_boot.domain.theater.dto.TheaterCreateCommand;
import com.ceos23.spring_boot.domain.theater.dto.TheaterInfo;
import com.ceos23.spring_boot.domain.theater.dto.TheaterUpdateCommand;
import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.domain.theater.repository.TheaterRepository;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheaterService {
    private final TheaterRepository theaterRepository;

    @Cacheable(value = "theatersAll", key = "'all'")
    public List<TheaterInfo> findAllTheaters() {
        List<Theater> theaters = theaterRepository.findAllByDeletedAtIsNull();
        return theaters.stream()
                .map(TheaterInfo::from)
                .toList();
    }

    public List<TheaterInfo> searchTheaters(String location) {
        List<Theater> theaters = theaterRepository.findByLocationAndDeletedAtIsNull(location);
        return theaters.stream()
                .map(TheaterInfo::from)
                .toList();
    }

    @Cacheable(value = "theaters", key = "#id")
    public TheaterInfo findTheater(Long id) {
        Theater theater = findTheaterById(id);

        return TheaterInfo.from(theater);
    }

    private Theater findTheaterById(Long id) {
        return theaterRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.THEATER_NOT_FOUND));
    }

    @Transactional
    @CacheEvict(value = "theatersAll", key = "'all'")
    public TheaterInfo createTheater(TheaterCreateCommand command) {
        if (theaterRepository.existsByNameAndDeletedAtIsNull(command.name()))
            throw new BusinessException(ErrorCode.DUPLICATE_THEATER_NAME);

        Optional<Theater> deletedTheater = theaterRepository.findByNameAndDeletedAtIsNotNull(command.name());

        return deletedTheater
                .map(theater -> restoreTheater(theater, command))
                .orElseGet(() -> createNewTheater(command));
    }

    private TheaterInfo restoreTheater(Theater deletedTheater, TheaterCreateCommand command) {
        deletedTheater.restoreDelete();
        deletedTheater.update(command.name(), command.location());
        return TheaterInfo.from(deletedTheater);
    }

    private TheaterInfo createNewTheater(TheaterCreateCommand command) {
        Theater newTheater = Theater.builder()
                .name(command.name())
                .location(command.location())
                .build();
        theaterRepository.save(newTheater);
        return TheaterInfo.from(newTheater);
    }


    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "theaters", key = "#id"),
        @CacheEvict(value = "theatersAll", key = "'all'")
    })
    public TheaterInfo updateTheater(Long id, TheaterUpdateCommand command) {
        Theater theater = findTheaterById(id);

        validateDuplicateTheaterName(theater, command.name());

        theater.update(command.name(), command.location());

        return TheaterInfo.from(theater);
    }

    private void validateDuplicateTheaterName(Theater theater, String name) {
        if (!theater.getName().equals(name) && theaterRepository.existsByNameAndDeletedAtIsNull(name))
            throw new BusinessException(ErrorCode.DUPLICATE_THEATER_NAME);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "theaters", key = "#id"),
            @CacheEvict(value = "theatersAll", key = "'all'")
    })
    public void deleteTheater(Long id) {
        Theater theater = findTheaterById(id);

        theater.softDelete();
    }
}
