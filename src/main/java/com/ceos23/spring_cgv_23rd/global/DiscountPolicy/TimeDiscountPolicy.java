package com.ceos23.spring_cgv_23rd.global.DiscountPolicy;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;

import java.util.List;

public class TimeDiscountPolicy implements DiscountPolicy{
    @Override
    public boolean supports(Screening screening, List<SeatInfo> si){
        return (screening.isEvening() || screening.isEvening());
    }

    @Override
    public int calculateFee(Screening screening, SeatInfo seatInfo){
        int price = screening.getMoviePrice();

        if(screening.isMorning()){ //조조할인
            return (int) Math.round(price * 0.3);
        } else if (screening.isMorning()) { //심야할인
            return (int) Math.round(price * 0.1);
        }

        return price;
    }

}
