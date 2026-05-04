package com.ceos23.spring_cgv_23rd.Theater.Controller;

import com.ceos23.spring_cgv_23rd.Food.Domain.MenuType;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.MenuFindingResponseDTO;
import com.ceos23.spring_cgv_23rd.Theater.Service.TheaterMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class TheaterMenuController {
    private final TheaterMenuService theaterMenuService;

    /**
     * 극장별로 현재 팔고 있는 음식을 조회합니다.
     *
     * @param theaterId 극장ID
     * @param menuType 음식의 유형입니다. NEW("신제품"), COMBO("콤보"), POPCORN("팝콘"), BEVERAGE("음료"), SNACK("스낵"), CHARACTER("캐릭터굿즈");
     * @return 극장과 메뉴유형별 음식정보를 반환합니다.
     */
    @GetMapping(params = { "theaterId", "menuType" })
    public ResponseEntity<MenuFindingResponseDTO> findMenuByTheater(
            @RequestParam long theaterId,
            @RequestParam MenuType menuType
    ) {
        return ResponseEntity.ok(theaterMenuService.findMenuByTheater(theaterId, menuType));
    }
}
