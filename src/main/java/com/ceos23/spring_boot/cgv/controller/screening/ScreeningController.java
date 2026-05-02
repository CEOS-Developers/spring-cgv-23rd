package com.ceos23.spring_boot.cgv.controller.screening;

import com.ceos23.spring_boot.cgv.dto.screening.ScreeningResponse;
import com.ceos23.spring_boot.cgv.dto.screening.SeatAvailabilityResponse;
import com.ceos23.spring_boot.cgv.service.screening.ScreeningQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/screenings")
public class ScreeningController {

    private final ScreeningQueryService screeningQueryService;

    @GetMapping
    public List<ScreeningResponse> getScreenings(
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) Long cinemaId
    ) {
        return screeningQueryService.getScreenings(movieId, cinemaId).stream()
                .map(ScreeningResponse::from)
                .toList();
    }

    @GetMapping("/{screeningId}/seats")
    public SeatAvailabilityResponse getSeatAvailability(@PathVariable Long screeningId) {
        return screeningQueryService.getSeatAvailability(screeningId);
    }
}
