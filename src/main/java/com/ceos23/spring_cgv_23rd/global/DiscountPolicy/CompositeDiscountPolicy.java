package com.ceos23.spring_cgv_23rd.global.DiscountPolicy;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 모든 DiscountPolicy의 구현체를 List로 가지며 순차적으로 할인가를 계산합니다.
 */
@Component
public class CompositeDiscountPolicy implements DiscountPolicy {
    private final List<DiscountPolicy> policies;

    public CompositeDiscountPolicy(List<DiscountPolicy> policies){
        this.policies = policies;
    }

    /**
     * CompositeDiscountPolicy가 가진 DiscountPolicy들의 할인가능여부를 반환합니다.
     *
     * @param screening 예매할 영화의 상영정보
     * @param seatInfos 예매할 영화의 좌석정보
     * @return CompositeDiscountPolicy가 가진 DiscountPolicy 중에서 어느 하나라도 supports() 메서드에서 true를 반환한 경우 true를 반환합니다.
     */
    @Override
    public boolean supports(Screening screening, List<SeatInfo> seatInfos){
        return policies.stream()
                .anyMatch(p -> p.supports(screening, seatInfos));
    }

    /**
     * 상영정보에 속한 영화값에서 최종적으로 할인된 영화가격을 반환합니다.
     *
     * @param screening 예매할 영화의 상영정보
     * @param seatInfo 예매할 영화의 좌석정보
     * @return 최종적으로 할인된 영화가격
     */
    @Override
    public int calculateFee(Screening screening, SeatInfo seatInfo){
        int price = screening.getMoviePrice();

        for (DiscountPolicy dis : policies){
            if (dis.supports(screening, List.of(seatInfo))) {
                price -= dis.calculateFee(screening, seatInfo);
            }
        }

        return Math.max(price, 0);
    }
}
