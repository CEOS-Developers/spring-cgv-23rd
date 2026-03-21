package com.ceos23.spring_cgv_23rd.Theater.Controller;

import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.TheaterSearchAllResponseDTO;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.TheaterSearchResponseDTO;
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

    @GetMapping("/{searchQuery}")
    public ResponseEntity<TheaterSearchResponseDTO> searchWithName(
            @PathVariable String searchQuery
    ) {
        return theaterService.theaterSearchService(searchQuery);
    }

    @GetMapping
    public ResponseEntity<TheaterSearchAllResponseDTO> searchAll() {
        return theaterService.theaterSearchService();
    }
}