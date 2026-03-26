package com.ceos23.spring_boot.cgv.controller.movie;

import com.ceos23.spring_boot.cgv.global.security.CustomUserDetails;
import com.ceos23.spring_boot.cgv.service.movie.MovieLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
public class MovieLikeController {

    private final MovieLikeService movieLikeService;

    public MovieLikeController(MovieLikeService movieLikeService) {
        this.movieLikeService = movieLikeService;
    }

    @PostMapping("/{movieId}/likes")
    public ResponseEntity<Void> likeMovie(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long movieId
    ) {
        movieLikeService.likeMovie(userDetails.getUserId(), movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{movieId}/likes")
    public ResponseEntity<Void> unlikeMovie(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long movieId
    ) {
        movieLikeService.unlikeMovie(userDetails.getUserId(), movieId);
        return ResponseEntity.ok().build();
    }
}