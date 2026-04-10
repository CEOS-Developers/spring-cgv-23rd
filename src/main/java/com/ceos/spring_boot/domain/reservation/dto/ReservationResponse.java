package com.ceos.spring_boot.domain.reservation.dto;

import com.ceos.spring_boot.domain.reservation.entity.Reservation;
import com.ceos.spring_boot.domain.reservation.entity.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record ReservationResponse(
        @Schema(description = "예매 id", example = "1")
        Long reservationId,

        @Schema(description = "영화 제목", example = "왕과 사는 남자")
        String movieTitle,

        @Schema(description = "영화관 이름", example = "CGV 강변")
        String cinemaName,

        @Schema(description = "상영관 이름", example = "7관")
        String screenName,

        @Schema(description = "상영일", example = "2026-03-10")
        LocalDate showDate,

        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "상영 시작 시간", example = "14:30", type = "string")
        LocalTime startTime,

        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "상영 종료 시간", example = "16:30", type = "string")
        LocalTime endTime,

        @Schema(description = "예매 좌석", example = "[A10, A11]", type = "string")
        List<String> seats,

        // 예매 완료 시각
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "예매 일시", example = "2026-03-23 23:55:01", type = "string")
        LocalDateTime createdAt,

        // 예매 상태
        ReservationStatus status
) {
    public static ReservationResponse from(Reservation reservation) {
        LocalDateTime startAt = reservation.getSchedule().getStartAt();
        LocalDateTime endAt = reservation.getSchedule().getEndAt();

        return new ReservationResponse(
                reservation.getId(),
                reservation.getSchedule().getMovie().getTitle(),
                reservation.getSchedule().getScreen().getCinema().getName(),
                reservation.getSchedule().getScreen().getName(),
                startAt.toLocalDate(),
                startAt.toLocalTime(),
                endAt.toLocalTime(),
                reservation.getReservationSeats().stream()
                        .map(rs -> rs.getSeat().getSeatRow() + "-" + rs.getSeat().getSeatCol())
                        .toList(),
                reservation.getCreatedAt(),
                reservation.getStatus()
        );
    }
}