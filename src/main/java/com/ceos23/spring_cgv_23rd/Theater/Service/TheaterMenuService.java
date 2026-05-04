package com.ceos23.spring_cgv_23rd.Theater.Service;

import com.ceos23.spring_cgv_23rd.Food.Domain.MenuType;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.MenuFindingResponseDTO;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.MenuWrapperDTO;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterMenuRepository;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TheaterMenuService {
    private final TheaterRepository theaterRepository;
    private final TheaterMenuRepository theaterMenuRepository;

    /**
     * 극장별로 현재 팔고 있는 음식을 조회합니다.
     *
     * @param theaterId 극장ID
     * @param menuType 음식의 유형입니다. NEW("신제품"), COMBO("콤보"), POPCORN("팝콘"), BEVERAGE("음료"), SNACK("스낵"), CHARACTER("캐릭터굿즈");
     * @return 극장과 메뉴유형별 음식정보를 반환합니다.
     */
    @Transactional(readOnly = true)
    public MenuFindingResponseDTO findMenuByTheater(long theaterId, MenuType menuType){
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_THEATER)
        );

        List<TheaterMenu> tms = theaterMenuRepository.findByTheater(theater).stream()
                .filter(r -> r.getFood().getMenuType().equals(menuType)).toList();

        String items = tms.stream()
                        .map(m -> "id: " + m.getId() + ", soldOut: " + m.isSoldOut() + ", sold: " + m.getSold())
                .collect(Collectors.joining(" | "));

        log.debug("재고조회: 영화관={}, 메뉴타입={}, 결과=[{}]", theaterId, menuType, items);
        return MenuFindingResponseDTO.create(
                theater, MenuWrapperDTO.create(tms)
        );
    }
}
