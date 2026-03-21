package com.ceos23.spring_cgv_23rd.Theater.Service;

import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.TheaterSearchAllResponseDTO;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.TheaterSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TheaterService {
    TheaterRepository theaterRepository;

    TheaterService(TheaterRepository theaterRepository){
        this.theaterRepository = theaterRepository;
    }

    @Transactional
    public ResponseEntity<TheaterSearchResponseDTO> theaterSearchService(String query){
        Theater searchedTheater = theaterRepository.findByName(query).orElseThrow(NullPointerException::new);
        TheaterSearchResponseDTO responseDTO = TheaterSearchResponseDTO.builder()
                .theater(searchedTheater)
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    @Transactional
    public ResponseEntity<TheaterSearchAllResponseDTO> theaterSearchService(){
        List<Theater> searchedTheaters = theaterRepository.findAll();

        TheaterSearchAllResponseDTO responseDTO = TheaterSearchAllResponseDTO.builder()
                .searchedTheaters(searchedTheaters)
                .build();

        return ResponseEntity.ok(responseDTO);
    }
}
