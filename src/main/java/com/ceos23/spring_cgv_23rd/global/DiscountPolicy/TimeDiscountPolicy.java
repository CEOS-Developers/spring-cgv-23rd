package com.ceos23.spring_cgv_23rd.global.DiscountPolicy;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;

import java.util.List;

/**
 * 조조할인, 심야할인 등 시간대에 따른 할인여부 및 할인가격을 반환합니다.
 */
public class TimeDiscountPolicy implements DiscountPolicy{
    /**
     * 예매할 영화의 할인여부를 반환합니다.
     *
     * @param screening 예매할 영화의 상영정보
     * @param si 예매할 영화의 좌석정보
     * @return 할인이 가능할 경우 true, 아닐 경우 false를 반환합니다.
     */
    @Override
    public boolean supports(Screening screening, List<SeatInfo> si){
        return (screening.isMorning() || screening.isEvening());
    }

    /**
     * 영화의 할인가를 계산합니다.
     *
     * @param screening 예매할 영화의 상영정보
     * @param seatInfo 예매할 영화의 좌석정보
     * @return 할인가격
     */
    @Override
    public int calculateFee(Screening screening, SeatInfo seatInfo){
        int price = screening.getMoviePrice();

        if(screening.isMorning()){ //조조할인
            return (int) Math.round(price * 0.3);
        } else if (screening.isEvening()) { //심야할인
            return (int) Math.round(price * 0.1);
        }

        return 0;
    }

}
