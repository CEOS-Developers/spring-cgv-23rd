package com.ceos23.spring_cgv_23rd.global.DiscountPolicy;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 사용자의 요건에 따라서 할인조건을 검사합니다. 경로할인과 소아할인을 지원합니다.
 */
@Component
public class AudienceDiscountPolicy implements DiscountPolicy{
    /**
     * 좌석정보에 따라서 할인가능여부를 반환합니다.
     *
     * @param sc 예매할 영화 상영관의 정보
     * @param sis 예매할 영화의 좌석 정보
     * @return 경로할인 또는 소아할인이 가능한 경우 true, 이외에 false를 반환합니다.
     */
    @Override
    public boolean supports(Screening sc, List<SeatInfo> sis){
        for (SeatInfo si : sis){
            if (si.getInfo().contains("경로") || si.getInfo().contains("어린이")){
                return true;
            }
        }

        return false;
    }

    /**
     * 할인가를 게산합니다.
     *
     * @param screening 예매할 영화 상영관의 정보
     * @param seatInfo 예매할 영화의 좌석 정보
     * @return 할인될 금액을 반환합니다.
     */
    @Override
    public int calculateFee(Screening screening, SeatInfo seatInfo){
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
