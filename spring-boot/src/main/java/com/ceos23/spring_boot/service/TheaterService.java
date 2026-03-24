package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.Theater;
import com.ceos23.spring_boot.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheaterService {

    private final TheaterRepository theaterRepository;

    public List<Theater> findAll() {
        return theaterRepository.findAll();
    }

    public Theater findById(Long id) {
        return theaterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("영화관 없음"));
    }
}