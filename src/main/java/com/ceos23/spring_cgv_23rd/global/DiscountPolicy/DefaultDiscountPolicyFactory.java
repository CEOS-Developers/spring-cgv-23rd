package com.ceos23.spring_cgv_23rd.global.DiscountPolicy;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DiscountPolicy를 반환합니다.
 */
@Component
public class DefaultDiscountPolicyFactory implements DiscountPolicyFactory {

    private final List<DiscountPolicy> policies;

    public DefaultDiscountPolicyFactory(List<DiscountPolicy> policies) {
        this.policies = policies;
    }

    /**
     * CompositeDiscountPolicy를 반환합니다.
     * @param screening 예매할 영화의 상영정보
     * @param seatInfos 예매할 영화의 좌석정보
     * @return 생성된 DiscountPolicy (기본값: CompositeDiscountPolicy)
     */
    @Override
    public DiscountPolicy create(Screening screening, List<SeatInfo> seatInfos) {
        return new CompositeDiscountPolicy(
                policies.stream()
                        .filter(p -> p.supports(screening, seatInfos))
                        .toList()
        );
    }

    public DiscountPolicy createAllPolicy(Screening s, List<SeatInfo> seatInfos){
        return create(s, seatInfos);
    }
}