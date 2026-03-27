package com.ceos23.spring_cgv_23rd.Theater.Service;

import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.TheaterWrapperDTO;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.TheaterSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Region;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class TheaterService {
    TheaterRepository theaterRepository;

    TheaterService(TheaterRepository theaterRepository){
        this.theaterRepository = theaterRepository;
    }

    /**
     * 검색어로 극장 조회
     *
     * @param query 검색어
     * @return 검색결과. 극장의 id값과 이름값
     */
    @Transactional
    public ResponseEntity<TheaterSearchResponseDTO> theaterSearchService(String query){
        List<Theater> searchedTheater = theaterRepository.findByNameContaining(query);

        TheaterSearchResponseDTO responseDTO = TheaterSearchResponseDTO.builder()
                .theater(TheaterWrapperDTO.create(searchedTheater))
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 극장 전체조회
     *
     * @return 전체 극장의 id값과 이름값
     */
    @Transactional(readOnly = true)
    public ResponseEntity<TheaterSearchResponseDTO> theaterSearchService(){
        List<Theater> searchedTheaters = theaterRepository.findAll();

        TheaterSearchResponseDTO responseDTO = TheaterSearchResponseDTO.builder()
                .theater(TheaterWrapperDTO.create(searchedTheaters))
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 지역별 극장 조회(경기, 서울 등등...)
     *
     * @param reg 지역명
     *            반드시 SEOUL, GYEONGGI, INCHEON, GANGWON, CHUNGCHEONG, DAEGU, BUSAN_ULSAN, GYEONGSANG, HONAM_JEJU
     *            중 하나여야함.
     * @return 영화관 검색결과. id값과 이름 필드
     */
    @Transactional(readOnly = true)
    public ResponseEntity<TheaterSearchResponseDTO> theaterSearchService(Region reg){
        List<Theater> searchedTheaters = theaterRepository.findByRegion(reg);

        TheaterSearchResponseDTO responseDTO = TheaterSearchResponseDTO.builder()
                .theater(TheaterWrapperDTO.create(searchedTheaters))
                .build();

        return ResponseEntity.ok(responseDTO);
    }
}
