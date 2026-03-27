package com.ceos23.spring_cgv_23rd.DiscountPolicy;

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
    public int calculateDiscount(Screening screening, SeatInfo seatInfo){
        int discountMoney = 0;

        for (DiscountPolicy dis : policies){
            discountMoney += dis.calculateDiscount(screening, seatInfo);
        }

        return discountMoney;
    }
}
