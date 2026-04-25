package com.ceos.spring_cgv_23rd.domain.reservation.domain;

public class ReservationPolicy {

    /**
     * 예매 후 결제까지 좌석을 잡아두는 유효 시간 (초)
     */
    public static final long HOLD_TTL_SECONDS = 300;


    private ReservationPolicy() {
    }
}
