package com.ceos23.spring_boot.cgv.controller.cinema;

import com.ceos23.spring_boot.cgv.dto.cinema.CinemaResponse;
import com.ceos23.spring_boot.cgv.service.cinema.CinemaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cinemas")
public class CinemaController {

    private final CinemaService cinemaService;

    @GetMapping
    public List<CinemaResponse> getCinemas() {
        return cinemaService.getCinemas().stream()
                .map(CinemaResponse::from)
                .toList();
    }

    @GetMapping("/{cinemaId}")
    public CinemaResponse getCinema(@PathVariable Long cinemaId) {
        return CinemaResponse.from(cinemaService.getCinema(cinemaId));
    }
}