package com.ceos23.spring_boot.cgv.controller.cinema;

import com.ceos23.spring_boot.cgv.dto.cinema.CinemaLikeResponse;
import com.ceos23.spring_boot.cgv.global.security.CustomUserDetails;
import com.ceos23.spring_boot.cgv.service.cinema.CinemaLikeService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cinemas")
public class CinemaLikeController {

    private final CinemaLikeService cinemaLikeService;

    public CinemaLikeController(CinemaLikeService cinemaLikeService) {
        this.cinemaLikeService = cinemaLikeService;
    }

    @PostMapping("/{cinemaId}/likes")
    public ResponseEntity<Void> likeCinema(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cinemaId
    ) {
        cinemaLikeService.likeCinema(userDetails.getUserId(), cinemaId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cinemaId}/likes")
    public ResponseEntity<Void> unlikeCinema(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cinemaId
    ) {
        cinemaLikeService.unlikeCinema(userDetails.getUserId(), cinemaId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/likes")
    public ResponseEntity<List<CinemaLikeResponse>> getLikedCinemas(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<CinemaLikeResponse> responses = cinemaLikeService.getLikedCinemas(userDetails.getUserId())
                .stream()
                .map(CinemaLikeResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }
}
