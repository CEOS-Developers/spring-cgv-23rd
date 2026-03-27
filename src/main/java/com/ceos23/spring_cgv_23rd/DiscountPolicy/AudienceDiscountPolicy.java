package com.ceos23.spring_cgv_23rd.DiscountPolicy;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;

import java.util.List;

public class AudienceDiscountPolicy implements DiscountPolicy{
    @Override
    public boolean supports(Screening sc, List<SeatInfo> sis){
        for (SeatInfo si : sis){
            if (si.getInfo().contains("경로") || si.getInfo().contains("어린이")){
                return true;
            }
        }

        return false;
    }

    @Override
    public int calculateDiscount(Screening screening, SeatInfo seatInfo){
        int price = screening.getMoviePrice();

        switch (seatInfo){
            case CHILD -> {
                return (int) Math.round(price * 0.3);
            } case SENIOR -> {
                return (int) Math.round(price * 0.2);
            }
            default -> {
                return 0;
            }
        }

    }
}
