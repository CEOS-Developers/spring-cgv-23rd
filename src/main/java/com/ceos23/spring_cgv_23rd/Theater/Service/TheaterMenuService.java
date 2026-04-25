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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheaterMenuService {
    TheaterRepository theaterRepository;
    TheaterMenuRepository theaterMenuRepository;

    /**
     * 극장별 메뉴 조회
     */
    public MenuFindingResponseDTO findMenuByTheater(long theaterId, MenuType menuType){
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_THEATER)
        );

        List<TheaterMenu> tms = theaterMenuRepository.findByTheater(theater).stream()
                .filter(r -> r.getFood().getMenuType().equals(menuType)).toList();

        return MenuFindingResponseDTO.create(
                theater, MenuWrapperDTO.create(tms)
        );
    }
}
