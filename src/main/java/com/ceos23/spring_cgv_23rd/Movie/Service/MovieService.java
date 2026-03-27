package com.ceos23.spring_cgv_23rd.Movie.Service;

import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.LikedMovieResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchAllResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieWrapperDTO;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Movie.Repository.BookmarkedMovieRepository;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.*;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.User.Domain.BookmarkedMovie;
import com.ceos23.spring_cgv_23rd.User.Domain.BookmarkedTheater;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final BookmarkedMovieRepository bookmarkedMovieRepository;

    MovieService(MovieRepository movieRepository, UserRepository userRepository, BookmarkedMovieRepository bookmarkedMovieRepository){
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.bookmarkedMovieRepository = bookmarkedMovieRepository;
    }

    @Transactional
    public ResponseEntity<MovieSearchResponseDTO> theaterSearchService(String query){
        List<Movie> searchedMovie = movieRepository.findByMovieNameContaining(query);

        MovieSearchResponseDTO responseDTO = MovieSearchResponseDTO.builder()
                .movie(MovieWrapperDTO.create(searchedMovie))
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    @Transactional
    public ResponseEntity<MovieSearchAllResponseDTO> theaterSearchService(){
        List<Movie> searchedMovies = movieRepository.findAll();

        MovieSearchAllResponseDTO responseDTO = MovieSearchAllResponseDTO.builder()
                .searchedMovies(MovieWrapperDTO.create(searchedMovies))
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 영화 좋아요
     *
     * 이미 눌려져있는데 한 번 더 누르면 취소
     */
    public LikedMovieResponseDTO movieLikService(long userId, long movieId){
        Movie movie = movieRepository.findById(movieId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "영화가 존재하지 않습니다."));

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자 정보가 존재하지 않습니다."));

        Optional<BookmarkedMovie> movieOptional = bookmarkedMovieRepository.findByMovieAndUser(movie, user);

        if (movieOptional.isPresent()){ //이미 있음, 취소
            bookmarkedMovieRepository.deleteBookmarkedMovieById(movieOptional.get().getId());

            return LikedMovieResponseDTO.create(
                    RequestType.DELETE, movieOptional.get().getMovie()
            );
        } else { //없음, 새로이 예약
            BookmarkedMovie bmm = BookmarkedMovie.create(user, movie);
            bookmarkedMovieRepository.save(bmm);

            return LikedMovieResponseDTO.create(
                    RequestType.ADD, movie
            );
        }
    }

    /**
     * 북마크한 영화 조회
     */
    public CheckLikedMovieResponseDTO checkLikedMovieByUserId(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자 정보가 존재하지 않습니다."));

        List<BookmarkedMovie> bmm = bookmarkedMovieRepository.findByUser(user);

        List<Movie> movies = bmm.stream()
                .map(BookmarkedMovie::getMovie)
                .toList();

        return CheckLikedMovieResponseDTO.crate(
                user, MovieWrapperDTO.create(movies)
        );
    }
}
