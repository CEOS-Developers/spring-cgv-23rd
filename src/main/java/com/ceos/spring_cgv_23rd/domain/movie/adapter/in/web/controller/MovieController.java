package com.ceos.spring_cgv_23rd.domain.movie.adapter.in.web.controller;

import com.ceos.spring_cgv_23rd.domain.movie.adapter.in.web.dto.response.MovieResponse;
import com.ceos.spring_cgv_23rd.domain.movie.adapter.in.web.mapper.MovieRequestMapper;
import com.ceos.spring_cgv_23rd.domain.movie.adapter.in.web.mapper.MovieResponseMapper;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.command.ToggleMovieLikeCommand;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.ToggleMovieLikeResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.port.in.GetMovieChartUseCase;
import com.ceos.spring_cgv_23rd.domain.movie.application.port.in.GetMovieDetailUseCase;
import com.ceos.spring_cgv_23rd.domain.movie.application.port.in.ToggleMovieLikeUseCase;
import com.ceos.spring_cgv_23rd.domain.movie.domain.Movie;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieCredit;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieMedia;
import com.ceos.spring_cgv_23rd.global.annotation.LoginUser;
import com.ceos.spring_cgv_23rd.global.apiPayload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
@Tag(name = "Movie", description = "영화 관련 API")
public class MovieController {

    private final GetMovieChartUseCase getMovieChartUseCase;
    private final GetMovieDetailUseCase getMovieDetailUseCase;
    private final ToggleMovieLikeUseCase toggleMovieLikeUseCase;
    private final MovieRequestMapper requestMapper;
    private final MovieResponseMapper responseMapper;

    @Operation(summary = "무비차트 조회")
    @GetMapping("/chart")
    public ApiResponse<List<MovieResponse.MovieListResponse>> getMovieChart() {
        List<Movie> movies = getMovieChartUseCase.getMovieChart();
        List<MovieResponse.MovieListResponse> response = responseMapper.toMovieListResponse(movies);

        return ApiResponse.onSuccess("무비차트 조회 성공", response);
    }

    @Operation(summary = "현재 상영중인 영화 조회")
    @GetMapping("/running")
    public ApiResponse<List<MovieResponse.MovieListResponse>> getRunningMovie() {
        List<Movie> movies = getMovieChartUseCase.getRunningMovies();
        List<MovieResponse.MovieListResponse> response = responseMapper.toMovieListResponse(movies);

        return ApiResponse.onSuccess("현재 상영중인 영화 조회 성공", response);
    }

    @Operation(summary = "상영 예정 영화 조회")
    @GetMapping("/upcoming")
    public ApiResponse<List<MovieResponse.MovieListResponse>> getUpcomingMovie() {
        List<Movie> movies = getMovieChartUseCase.getUpcomingMovies();
        List<MovieResponse.MovieListResponse> response = responseMapper.toMovieListResponse(movies);

        return ApiResponse.onSuccess("상영 예정 영화 조회 성공", response);
    }

    @Operation(summary = "영화 상세 조회")
    @GetMapping("/{movieId}")
    public ApiResponse<MovieResponse.MovieDetailResponse> getMovieDetail(
            @PathVariable Long movieId) {
        Movie movie = getMovieDetailUseCase.getMovieDetail(movieId);
        MovieResponse.MovieDetailResponse response = responseMapper.toMovieDetailResponse(movie);

        return ApiResponse.onSuccess("영화 상세 조회 성공", response);
    }

    @Operation(summary = "영화 출연진 조회")
    @GetMapping("/{movieId}/credits")
    public ApiResponse<List<MovieResponse.MovieCreditResponse>> getMovieCredits(
            @PathVariable Long movieId) {
        List<MovieCredit> credits = getMovieDetailUseCase.getMovieCredits(movieId);
        List<MovieResponse.MovieCreditResponse> response = responseMapper.toMovieCreditResponse(credits);

        return ApiResponse.onSuccess("영화 출연진 조회 성공", response);
    }

    @Operation(summary = "영화 미디어 조회")
    @GetMapping("/{movieId}/medias")
    public ApiResponse<List<MovieResponse.MovieMediaResponse>> getMovieMedia(
            @PathVariable Long movieId) {
        List<MovieMedia> medias = getMovieDetailUseCase.getMovieMedia(movieId);
        List<MovieResponse.MovieMediaResponse> response = responseMapper.toMovieMediaResponse(medias);

        return ApiResponse.onSuccess("영화 미디어 조회 성공", response);
    }

    @Operation(summary = "영화 찜 토글")
    @PostMapping("/{movieId}/like")
    public ApiResponse<MovieResponse.MovieLikeResponse> toggleMovieLike(
            @LoginUser Long userId,
            @PathVariable Long movieId) {
        ToggleMovieLikeCommand command = requestMapper.toToggleLikeCommand(userId, movieId);
        ToggleMovieLikeResult result = toggleMovieLikeUseCase.execute(command);
        String message = result.liked() ? "영화 찜 등록 성공" : "영화 찜 취소 성공";
        MovieResponse.MovieLikeResponse response = responseMapper.toMovieLikeResponse(result);

        return ApiResponse.onSuccess(message, response);
    }

}
