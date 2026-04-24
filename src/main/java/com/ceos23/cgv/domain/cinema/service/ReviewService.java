package com.ceos23.cgv.domain.cinema.service;

import com.ceos23.cgv.domain.cinema.entity.Review;
import com.ceos23.cgv.domain.cinema.repository.ReviewRepository;
import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.repository.MovieRepository;
import com.ceos23.cgv.domain.user.entity.User;
import com.ceos23.cgv.domain.user.repository.UserRepository;
import com.ceos23.cgv.domain.cinema.dto.ReviewCreateRequest;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    /**
     * [POST] 관람평 작성
     */
    @Transactional
    public Review createReview(ReviewCreateRequest request) {
        User user = findUser(request.userId());
        Movie movie = findMovie(request.movieId());
        Review review = Review.create(user, movie, request.type(), request.content());

        return reviewRepository.save(review);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Movie findMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
    }

    /**
     * [GET] 특정 영화의 관람평 목록 조회
     */
    public List<Review> getReviewsByMovieId(Long movieId) {
        return reviewRepository.findByMovieId(movieId);
    }
}
