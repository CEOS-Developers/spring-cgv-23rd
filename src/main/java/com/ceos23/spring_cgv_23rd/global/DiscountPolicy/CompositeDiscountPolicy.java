package com.ceos23.spring_cgv_23rd.global.DiscountPolicy;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;

import java.util.List;

public class CompositeDiscountPolicy implements DiscountPolicy {
    private final List<DiscountPolicy> policies;

    public CompositeDiscountPolicy(List<DiscountPolicy> policies){
        this.policies = policies;
    }

    @Override
    public boolean supports(Screening screening, List<SeatInfo> seatInfos){
        return policies.stream()
                .anyMatch(p -> p.supports(screening, seatInfos));
    }

    @Override
    public int calculateFee(Screening screening, SeatInfo seatInfo){
        int price = screening.getMoviePrice();

        for (DiscountPolicy dis : policies){
            price -= dis.calculateFee(screening, seatInfo);
        }

        return price;
    }
}
