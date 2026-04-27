package com.ceos23.spring_cgv_23rd.global.DiscountPolicy;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;

import java.util.List;

public interface DiscountPolicy {
    int calculateFee(Screening screening, SeatInfo seatInfo);
    boolean supports(Screening screening, List<SeatInfo> seatInfo);
}
