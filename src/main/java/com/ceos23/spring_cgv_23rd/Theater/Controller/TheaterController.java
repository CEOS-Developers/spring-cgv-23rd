package com.ceos23.spring_cgv_23rd.Theater.Controller;

import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.CheckLikedTheaterResponseDTO;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.LikedTheaterResponseDTO;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.TheaterSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Region;
import com.ceos23.spring_cgv_23rd.Theater.Service.TheaterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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
     * @return 옳게 반환된경우 극장 ID과 이름이, DB 조회결과 검색되는 극장이 없다면 CustomException 발생
     */
    @GetMapping(params= {"query", "!region"})
    public ResponseEntity<TheaterSearchResponseDTO> searchWithName(
            @RequestParam String query
    ) {
        return ResponseEntity.ok(theaterService.theaterSearchService(query));
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
        return ResponseEntity.ok(theaterService.theaterSearchService(region));
    }

    /**
     * 극장 전체조회
     *
     * @return 전체조회결과
     */
    @GetMapping
    public ResponseEntity<TheaterSearchResponseDTO> searchAll() {
        return ResponseEntity.ok(theaterService.theaterSearchService());
    }

    /**
     * 영화관 찜하기 관련 기능입니다.
     * 사용자가 이미 찜한 영화관의 경우 취소, 찜하지 않은 경우 찜을 추가합니다.
     *
     * @param user 사용자입니다. 쿠키를 통해 자동으로 받는 값입니다.
     * @param theaterId 찜하거나 찜을 취소할 영화의 id입니다.
     * @return 찜/취소 여부, 극장이름, 극장ID를 반환합니다.
     */
    @PostMapping(value = "/likes", params = "theaterId")
    public ResponseEntity<LikedTheaterResponseDTO> likey(
            @RequestParam long theaterId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(theaterService.theaterBookMarkService(user.getUsername(), theaterId));
    }

    /**
     * 좋아요 조회
     */
    @GetMapping(value = "/likes")
    public ResponseEntity<CheckLikedTheaterResponseDTO> likey(
            @AuthenticationPrincipal User user
            ){
        return ResponseEntity.ok(theaterService.checkTheaterBookMark(user.getUsername()));
    }

}