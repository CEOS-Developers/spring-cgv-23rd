package com.ceos23.spring_cgv_23rd.Screen.Controller;

import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Screen.Service.ScreeningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@RestController
@RequestMapping("/api/screen")
public class ScreenController {
    private final ScreeningService screeningService;

    public ScreenController(ScreeningService screeningService){
        this.screeningService = screeningService;
    }

    /**
     * 사용자가 영화관id, 관람일자를 요청에 담아 보내면
     * 영화관 별 이용가능한 시간 및 상영관에 대한 정보를 제공합니다.
     *
     * @param theaterId 확인할 영화관id
     * @param date 확인할 날짜
     * @return 상영관에 대한 정보
     */
    @GetMapping(params = {"theaterId", "date"})
    public ResponseEntity<List<ScreeningSearchResponseDTO>> screenSearching(
            @RequestParam long theaterId,
            @RequestParam LocalDate date
            ){
        LocalDateTime criteriaTime;

        if(LocalDate.now().equals(date)){
            criteriaTime = LocalDateTime.now();
        } else {
            criteriaTime = LocalDateTime.of(date, LocalTime.of(0,0));
        }

        return ResponseEntity.ok(screeningService.searchMovieWithTheaterId(theaterId, criteriaTime, LocalDateTime.of(date, LocalTime.of(23, 59, 59))));
    }

    /**
     * 사용자가 영화관id, 관람일자, 영화id를 요청에 담아 전송하면
     * 영화관 별 이용가능한 시간 및 상영관에 대한 정보를 제공합니다.
     *
     * @param theaterId 확인할 영화관id
     * @param movieId 확인할 영화id
     * @param date 확인할 날짜
     * @return 확인된 정보
     */
    @GetMapping(params = {"theaterId", "movieId", "date"})
    public ResponseEntity<ScreeningSearchResponseDTO> screenSearching(
            @RequestParam long theaterId,
            @RequestParam long movieId,
            @RequestParam LocalDate date
    ){
        LocalDateTime criteriaTime;

        if(LocalDate.now().equals(date)){
            criteriaTime = LocalDateTime.now();
        } else {
            criteriaTime = LocalDateTime.of(date, LocalTime.of(0,0));
        }

        return ResponseEntity.ok(screeningService.searchMovieWithTheaterId(theaterId, movieId, criteriaTime, LocalDateTime.of(date, LocalTime.of(23,59,59))));
    }
}
