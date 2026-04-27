package com.ceos23.spring_boot.controller.reservation.dto;

import jakarta.validation.constraints.NotNull;

public record ReservationCancelRequest(
        @NotNull(message = "회원 ID는 필수입니다.")
        Long userId
) {
    public Long toLong() {
        return this.userId;
    }
}
