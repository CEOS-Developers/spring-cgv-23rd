package com.ceos23.spring_cgv_23rd.Movie.Controller;

import com.ceos23.spring_cgv_23rd.Movie.DTO.Request.BookmarkMovieRequestDTO;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.LikedMovieResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchAllResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.Service.MovieService;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.CheckLikedMovieResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movie")
public class MovieController {
    MovieService movieService;

    public MovieController(MovieService movieService){
        this.movieService = movieService;
    }

    @GetMapping(params = "searchQuery")
    public ResponseEntity<MovieSearchResponseDTO> searchWithName(
            @RequestParam String searchQuery
    ) {
        return ResponseEntity.ok(movieService.theaterSearchService(searchQuery));
    }

    @GetMapping
    public ResponseEntity<MovieSearchAllResponseDTO> searchAll() {
        return ResponseEntity.ok(movieService.theaterSearchService());
    }

    /**
     * 영화 좋아요
     * TODO: DTO써서 body 반환하도록 바꾸기, 영화관 쪽 도메인에도 동일적용
     *
     * 이미 눌려져있는데 한 번 더 누르면 취소
     */
    @PostMapping("/likes")
    public LikedMovieResponseDTO bookmarkMovie(
            @RequestBody BookmarkMovieRequestDTO bmrDTO
            ) {
        return movieService.movieLikService(bmrDTO);
    }

    /**
     * 북마크한 영화 조회
     */
    @GetMapping("/likes")
    public ResponseEntity<CheckLikedMovieResponseDTO> getBookmarkMovieByUser(
            @AuthenticationPrincipal User user
            ){
        return ResponseEntity.ok(movieService.checkLikedMovieByUserId(user.getUsername()));
    }

}