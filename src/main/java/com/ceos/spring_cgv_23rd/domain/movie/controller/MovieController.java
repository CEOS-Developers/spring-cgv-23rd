package com.ceos.spring_cgv_23rd.domain.movie.controller;

import com.ceos.spring_cgv_23rd.domain.movie.dto.MovieResponseDTO;
import com.ceos.spring_cgv_23rd.domain.movie.service.MovieService;
import com.ceos.spring_cgv_23rd.global.apiPayload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
@Tag(name = "Movie", description = "영화 관련 API")
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "무비차트 조회")
    @GetMapping("/chart")
    public ApiResponse<List<MovieResponseDTO.MovieListResponseDTO>> getMovieChart() {
        List<MovieResponseDTO.MovieListResponseDTO> response = movieService.getMovieChart();
        return ApiResponse.onSuccess("무비차트 조회 성공", response);
    }

    @Operation(summary = "현재 상영중인 영화 조회")
    @GetMapping("/running")
    public ApiResponse<List<MovieResponseDTO.MovieListResponseDTO>> getRunningMovie() {
        List<MovieResponseDTO.MovieListResponseDTO> response = movieService.getRunningMovie();
        return ApiResponse.onSuccess("현재 상영중인 영화 조회 성공", response);
    }

    @Operation(summary = "상영 예정 영화 조회")
    @GetMapping("/upcoming")
    public ApiResponse<List<MovieResponseDTO.MovieListResponseDTO>> getUpcomingMovie() {
        List<MovieResponseDTO.MovieListResponseDTO> response = movieService.getUpcomingMovie();
        return ApiResponse.onSuccess("상영 예정 영화 조회 성공", response);
    }

    @Operation(summary = "영화 상세 조회")
    @GetMapping("/{movieId}")
    public ApiResponse<MovieResponseDTO.MovieDetailResponseDTO> getMovieDetail(
            @PathVariable Long movieId) {
        MovieResponseDTO.MovieDetailResponseDTO response = movieService.getMovieDetail(movieId);
        return ApiResponse.onSuccess("영화 상세 조회 성공", response);
    }

    @Operation(summary = "영화 출연진 조회")
    @GetMapping("/{movieId}/credits")
    public ApiResponse<List<MovieResponseDTO.MovieCreditResponseDTO>> getMovieCredits(
            @PathVariable Long movieId) {
        List<MovieResponseDTO.MovieCreditResponseDTO> response = movieService.getMovieCredits(movieId);
        return ApiResponse.onSuccess("영화 출연진 조회 성공", response);
    }

    @Operation(summary = "영화 미디어 조회")
    @GetMapping("/{movieId}/medias")
    public ApiResponse<List<MovieResponseDTO.MovieMediaResponseDTO>> getMovieMedia(
            @PathVariable Long movieId) {
        List<MovieResponseDTO.MovieMediaResponseDTO> response = movieService.getMovieMedias(movieId);
        return ApiResponse.onSuccess("영화 미디어 조회 성공", response);
    }

}
