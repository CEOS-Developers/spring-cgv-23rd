package com.ceos23.spring_boot.cgv.service.movie;

import com.ceos23.spring_boot.cgv.domain.like.MovieLike;
import com.ceos23.spring_boot.cgv.domain.movie.Movie;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.like.MovieLikeRepository;
import com.ceos23.spring_boot.cgv.repository.movie.MovieRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MovieLikeService {

    private final MovieLikeRepository movieLikeRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public MovieLikeService(
            MovieLikeRepository movieLikeRepository,
            MovieRepository movieRepository,
            UserRepository userRepository
    ) {
        this.movieLikeRepository = movieLikeRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public void likeMovie(Long userId, Long movieId) {
        if (movieLikeRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new ConflictException(ErrorCode.CONFLICT);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MOVIE_NOT_FOUND));

        MovieLike movieLike = new MovieLike(user, movie);
        movieLikeRepository.save(movieLike);
    }

    public void unlikeMovie(Long userId, Long movieId) {
        MovieLike movieLike = movieLikeRepository.findByUserIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));

        movieLikeRepository.delete(movieLike);
    }
}