package com.ceos23.spring_boot.cgv.controller.cinema;

import com.ceos23.spring_boot.cgv.dto.cinema.CinemaResponse;
import com.ceos23.spring_boot.cgv.service.cinema.CinemaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cinemas")
public class CinemaController {

    private final CinemaService cinemaService;

    @GetMapping
    public ResponseEntity<List<CinemaResponse>> getCinemas() {
        List<CinemaResponse> responses = cinemaService.getCinemas().stream()
                .map(CinemaResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{cinemaId}")
    public ResponseEntity<CinemaResponse> getCinema(@PathVariable Long cinemaId) {
        CinemaResponse response = CinemaResponse.from(cinemaService.getCinema(cinemaId));
        return ResponseEntity.ok(response);
    }
}