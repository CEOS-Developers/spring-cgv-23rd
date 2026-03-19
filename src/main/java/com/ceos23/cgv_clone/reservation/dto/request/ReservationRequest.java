package com.ceos23.cgv_clone.reservation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReservationRequest {

    @NotNull(message = "상영 일정 id는 필수입니다.")
    private Long scheduleId;

    @NotEmpty(message = "최소 1개 이상의 좌석을 선택해 주세요.")
    private List<String> seats;
}
