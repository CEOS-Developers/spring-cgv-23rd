package com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result;

import java.time.LocalDate;

public record GuestInfoResult(
        Long guestId,
        String phone,
        LocalDate birth,
        String password
) {
}
