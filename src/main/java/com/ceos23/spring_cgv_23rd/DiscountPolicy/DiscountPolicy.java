package com.ceos23.spring_cgv_23rd.DiscountPolicy;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;

import java.util.List;

public interface DiscountPolicy {
    int calculateDiscount(Screening screening, SeatInfo seatInfo);
    boolean supports(Screening screening, List<SeatInfo> seatInfo);
}
