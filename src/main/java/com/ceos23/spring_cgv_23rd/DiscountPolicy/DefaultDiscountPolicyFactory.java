package com.ceos23.spring_cgv_23rd.DiscountPolicy;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultDiscountPolicyFactory implements DiscountPolicyFactory {

    private final List<DiscountPolicy> policies;

    public DefaultDiscountPolicyFactory(List<DiscountPolicy> policies) {
        this.policies = policies;
    }

    @Override
    public DiscountPolicy create(Screening screening, List<SeatInfo> seatInfos) {
        return new CompositeDiscountPolicy(
                policies.stream()
                        .filter(p -> p.supports(screening, seatInfos))
                        .toList()
        );
    }
}