package com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

public class ReservationRequest {

    @Builder
    public record CreateReservationRequest(

            @NotNull(message = "상영 ID가 없습니다.")
            Long screeningId,

            @NotEmpty(message = "좌석을 선택해주세요.")
            List<Long> seatIds
    ) {
    }

    @Builder
    public record ConfirmReservationRequest(


            @NotNull(message = "screeningId가 없습니다.")
            Long screeningId,

            @NotEmpty(message = "seatIds가 없습니다.")
            List<Long> seatIds
    ) {
    }
}
