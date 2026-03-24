package com.ceos23.cgv_clone.service;

import com.ceos23.cgv_clone.common.ApiResponse;
import com.ceos23.cgv_clone.theater.domain.Theater;
import com.ceos23.cgv_clone.theater.dto.response.TheaterResponse;
import com.ceos23.cgv_clone.theater.repository.TheaterRepository;
import com.ceos23.cgv_clone.theater.service.TheaterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TheaterServiceTest {

    @InjectMocks
    private TheaterService theaterService;

    @Mock
    private TheaterRepository theaterRepository;

    @Test
    @DisplayName("존재하는 영화관 조회 시 상세 정보 반환")
    void 영화관조회_성공() {
        // given
        Long theaterId = 1L;
        Theater theater = Theater.builder()
                .name("CGV 강남")
                .region("서울")
                .address("서울시 강남구")
                .build();
        ReflectionTestUtils.setField(theater, "id", theaterId);

        given(theaterRepository.findById(theaterId)).willReturn(Optional.of(theater));

        // when
        ApiResponse<TheaterResponse> response = theaterService.getTheater(theaterId);

        // then
        assertTrue(response.isSuccess());
        assertEquals(200, response.getResultCode());
        assertEquals("SELECT SUCCESS", response.getResultMsg());
        assertNotNull(response.getResult());

        TheaterResponse result = response.getResult();
        assertEquals(theaterId, result.getId());
        assertEquals("CGV 강남", result.getName());
        assertEquals("서울", result.getRegion());
        assertEquals("서울시 강남구", result.getAddress());
    }

    @Test
    @DisplayName("region이 존재하고 조회 결과가 있으면 해당 지역 영화관 목록 반환")
    void 지역별조회_성공() {
        // given
        String region = "서울";
        Theater t1 = Theater.builder()
                .name("CGV 강남")
                .region("서울")
                .address("서울 강남구")
                .build();

        Theater t2 = Theater.builder()
                .name("CGV 용산")
                .region("서울")
                .address("서울 용산구")
                .build();

        ReflectionTestUtils.setField(t1, "id", 1L);
        ReflectionTestUtils.setField(t2, "id", 2L);

        given(theaterRepository.findAllByRegion(region)).willReturn(List.of(t1, t2));

        // when
        ApiResponse<List<TheaterResponse>> response = theaterService.getTheatersByRegion(region);

        // then
        assertTrue(response.isSuccess());
        assertEquals(200, response.getResultCode());
        assertEquals(2, response.getResult().size());
        assertEquals("CGV 강남", response.getResult().get(0).getName());
        assertEquals("CGV 용산", response.getResult().get(1).getName());
    }
}
