package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.Movie;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    // 영화 생성
    public Movie create(String title, String director) {
        return movieRepository.save(new Movie(title, director));
    }

    // 전체 조회
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    // 단건 조회
    public Movie findById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
    }
}