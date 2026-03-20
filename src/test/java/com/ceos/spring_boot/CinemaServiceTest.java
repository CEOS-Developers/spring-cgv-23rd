package com.ceos.spring_boot;

import com.ceos.spring_boot.domain.cinema.dto.CinemaListResponse;
import com.ceos.spring_boot.domain.cinema.dto.CinemaResponse;
import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.cinema.repository.CinemaRepository;
import com.ceos.spring_boot.domain.cinema.service.CinemaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CinemaServiceTest {

    @Mock
    private CinemaRepository cinemaRepository;

    @InjectMocks
    private CinemaService cinemaService;

    @Test
    @DisplayName("영화관 ID로 상세 조회 성공")
    void findCinemaById_success() {
        // given
        Long id = 1L;
        Cinema cinema = Cinema.builder().id(id).name("CGV 강남").build();
        when(cinemaRepository.findById(id)).thenReturn(Optional.of(cinema));

        // when
        CinemaResponse result = cinemaService.findCinemaById(id);

        // then
        assertThat(result.name()).isEqualTo("CGV 강남");
        verify(cinemaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("모든 영화관 목록 조회 성공")
    void findAllCinemas_success() {
        // given
        Cinema cinema = Cinema.builder().id(1L).name("CGV 압구정").build();
        when(cinemaRepository.findAll()).thenReturn(List.of(cinema));

        // when
        CinemaListResponse result = cinemaService.findAllCinemas();

        // then
        assertThat(result.cinemas()).isNotEmpty();
        verify(cinemaRepository, times(1)).findAll();
    }
}
