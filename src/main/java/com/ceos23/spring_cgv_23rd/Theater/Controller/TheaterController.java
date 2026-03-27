package com.ceos23.spring_cgv_23rd.Theater.Controller;

import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.TheaterSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Region;
import com.ceos23.spring_cgv_23rd.Theater.Service.TheaterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/theater")
public class TheaterController {
    TheaterService theaterService;

    public TheaterController(TheaterService theaterService){
        this.theaterService = theaterService;
    }

    /**
     * 검색어로 극장 조회
     *
     * @param query 검색어
     * @return 옳게 반환된경우 극장 ID과 이름이, DB 조회결과 검색되는 극장이 없다면 EntityNotFoundException 발생
     */
    @GetMapping(params= {"query", "!region"})
    public ResponseEntity<TheaterSearchResponseDTO> searchWithName(
            @RequestParam String query
    ) {
        return theaterService.theaterSearchService(query);
    }

    /**
     * 지역으로 극장 조회
     *
     * @param region 검색어
     * @return 옳게 반환된 경우 극장 ID와 이름이, Region 값이 잘못되면 ~~ 발생
     */
    @GetMapping(params= {"!query", "region"})
    public ResponseEntity<TheaterSearchResponseDTO> searchWithRegion(
            @RequestParam Region region
    ) {
        return theaterService.theaterSearchService(region);
    }

    /**
     * 극장 전체조회
     *
     * @return 극장id와 이름
     */
    @GetMapping
    public ResponseEntity<TheaterSearchResponseDTO> searchAll() {
        return theaterService.theaterSearchService();
    }

}