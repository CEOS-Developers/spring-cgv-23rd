package com.ceos23.spring_cgv_23rd.Screen.Controller;

import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Service.ScreeningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * TODO: ResponseEntity 작성 서비스계층에서 컨트롤러 계층으로 분리하기
 */
@RestController
@RequestMapping("/api/screen")
public class ScreenController {
    private final ScreeningService screeningService;

    public ScreenController(ScreeningService screeningService){
        this.screeningService = screeningService;
    }

    /**
     * 사용자가 영화관 id와 관림일자를 건네주면 영화관별로 이용가능한 시간 및 상영관에 대한 정보를 제공합니다.
     */
    @GetMapping(params = {"theaterId", "date"})
    public ResponseEntity<List<ScreeningSearchResponseDTO>> screenSearching(
            @RequestParam long theaterId,
            @RequestParam LocalDate date
            ){
        return ResponseEntity.ok(screeningService.searchMovieWithTheaterId(theaterId, date));
    }

    /**
     * 사용자가 영화관 id와 관림일자, 영화id를 건네주면 영화관별로 이용가능한 시간 및 상영관에 대한 정보를 제공합니다.
     */
    @GetMapping(params = {"theaterId", "movieId", "date"})
    public ResponseEntity<List<ScreeningSearchResponseDTO>> screenSearching(
            @RequestParam long theaterId,
            @RequestParam long movieId,
            @RequestParam LocalDate date
    ){
        return ResponseEntity.ok(screeningService.searchMovieWithTheaterId(theaterId, movieId, date));
    }
}
