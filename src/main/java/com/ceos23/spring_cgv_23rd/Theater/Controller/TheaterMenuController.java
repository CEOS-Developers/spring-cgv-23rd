package com.ceos23.spring_cgv_23rd.Theater.Controller;

import com.ceos23.spring_cgv_23rd.Food.Domain.MenuType;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.MenuFindingResponseDTO;
import com.ceos23.spring_cgv_23rd.Theater.Service.TheaterMenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
public class TheaterMenuController {
    TheaterMenuService theaterMenuService;

    public TheaterMenuController(TheaterMenuService theaterMenuService){
        this.theaterMenuService = theaterMenuService;
    }

    /**
     * 극장별 메뉴조회
     */
    @GetMapping(params = { "theaterId", "menuType" })
    public ResponseEntity<MenuFindingResponseDTO> findMenuByTheater(
            @RequestParam long theaterId,
            @RequestParam MenuType menuType
    ) {
        return ResponseEntity.ok(theaterMenuService.findMenuByTheater(theaterId, menuType));
    }
}
