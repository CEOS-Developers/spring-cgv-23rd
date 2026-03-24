package com.ceos.spring_cgv_23rd.domain.movie.service;

import com.ceos.spring_cgv_23rd.domain.movie.dto.MovieResponseDTO;
import com.ceos.spring_cgv_23rd.domain.movie.entity.Movie;
import com.ceos.spring_cgv_23rd.domain.movie.entity.MovieLike;
import com.ceos.spring_cgv_23rd.domain.movie.enums.MovieStatus;
import com.ceos.spring_cgv_23rd.domain.movie.exception.MovieErrorCode;
import com.ceos.spring_cgv_23rd.domain.movie.repository.*;
import com.ceos.spring_cgv_23rd.domain.user.entity.User;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieStatisticRepository movieStatisticRepository;
    private final MovieCreditRepository movieCreditRepository;
    private final MovieMediaRepository movieMediaRepository;
    private final MovieLikeRepository movieLikeRepository;

    @Override
    public List<MovieResponseDTO.MovieListResponseDTO> getMovieChart() {

        // 상영중/상영예정 영화 조회
        List<Movie> movies = movieRepository.findWithStatisticByStatusIn(
                List.of(MovieStatus.RUNNING, MovieStatus.UPCOMING));

        return movies.stream()
                .map(movie -> MovieResponseDTO.MovieListResponseDTO.of(movie, movie.getMovieStatistic()))
                .toList();
    }

    @Override
    public List<MovieResponseDTO.MovieListResponseDTO> getRunningMovie() {

        // 상영 중인 영화 조회
        List<Movie> movies = movieRepository.findWithStatisticByStatus(MovieStatus.RUNNING);

        return movies.stream()
                .map(movie -> MovieResponseDTO.MovieListResponseDTO.of(movie, movie.getMovieStatistic()))
                .toList();
    }

    @Override
    public List<MovieResponseDTO.MovieListResponseDTO> getUpcomingMovie() {

        // 상영 예정 영화 조회
        List<Movie> movies = movieRepository.findWithStatisticByStatus(MovieStatus.UPCOMING);

        return movies.stream()
                .map(movie -> MovieResponseDTO.MovieListResponseDTO.of(movie, movie.getMovieStatistic()))
                .toList();
    }

    @Override
    public MovieResponseDTO.MovieDetailResponseDTO getMovieDetail(Long movieId) {

        // 영화 조회
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND));


        return MovieResponseDTO.MovieDetailResponseDTO.of(movie, movie.getMovieStatistic());
    }

    @Override
    public List<MovieResponseDTO.MovieCreditResponseDTO> getMovieCredits(Long movieId) {

        if (!movieRepository.existsById(movieId)) {
            throw new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND);
        }

        return movieCreditRepository.findByMovieIdWithContributor(movieId).stream()
                .map(MovieResponseDTO.MovieCreditResponseDTO::from)
                .toList();
    }

    @Override
    public List<MovieResponseDTO.MovieMediaResponseDTO> getMovieMedias(Long movieId) {

        if (!movieRepository.existsById(movieId)) {
            throw new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND);
        }

        return movieMediaRepository.findByMovieId(movieId).stream()
                .map(MovieResponseDTO.MovieMediaResponseDTO::from)
                .toList();
    }

    @Override
    @Transactional
    public MovieResponseDTO.MovieLikeResponseDTO toggleMovieLike(Long userId, Long movieId) {

        // TODO : 주석 제거
        // 유저 조회
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));
        User user = User.builder()
                .id(userId)
                .build();

        // 영화 조회
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND));

        Optional<MovieLike> existingLike = movieLikeRepository.findByUserIdAndMovieId(userId, movieId);

        boolean liked;
        if (existingLike.isPresent()) {
            // 찜 취소
            movieLikeRepository.delete(existingLike.get());
            liked = false;
        } else {
            // 찜 등록
            movieLikeRepository.save(MovieLike.createMovieLike(user, movie));
            liked = true;
        }

        return MovieResponseDTO.MovieLikeResponseDTO.of(movieId, liked);
    }
}
