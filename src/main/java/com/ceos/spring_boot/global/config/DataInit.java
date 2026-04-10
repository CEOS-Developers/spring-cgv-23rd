package com.ceos.spring_boot.global.config;

import com.ceos.spring_boot.domain.cinema.entity.ScreenType;
import com.ceos.spring_boot.domain.cinema.entity.Seat;
import com.ceos.spring_boot.domain.cinema.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final SeatRepository seatRepository;

    // 좌석 생성 코드
    @Override
    public void run(String... args) {
        // DB에 좌석이 하나도 없을 때만 초기화
        if (seatRepository.count() == 0) {
            // 일반관 좌석 생성
            createSeats(ScreenType.GENERAL, 6, 5);
            // 특별관 좌석 생성
            createSeats(ScreenType.SPECIAL, 4, 3);
        }
    }

    private void createSeats(ScreenType type, int rows, int cols) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 1; j <= cols; j++) {
                String rowChar = String.valueOf((char)('A' + i));

                seats.add(Seat.builder()
                        .screenType(type)
                        .seatRow(rowChar)
                        .seatCol(j)
                        .build());
            }
        }
        seatRepository.saveAll(seats);
    }
}