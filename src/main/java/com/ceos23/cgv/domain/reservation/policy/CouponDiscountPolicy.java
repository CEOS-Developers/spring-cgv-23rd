package com.ceos23.cgv.domain.reservation.policy;

import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;

public final class CouponDiscountPolicy {

    private static final String WELCOME_CGV = "WELCOME_CGV";
    private static final String VIP_HALF_PRICE = "VIP_HALF_PRICE";
    private static final int WELCOME_DISCOUNT = 3000;

    private CouponDiscountPolicy() {
    }

    public static int apply(int currentPrice, String couponCode) {
        if (!hasCoupon(couponCode)) {
            return currentPrice;
        }

        int discountAmount = calculateDiscountAmount(currentPrice, couponCode);
        return Math.max(currentPrice - discountAmount, 0);
    }

    private static boolean hasCoupon(String couponCode) {
        return couponCode != null && !couponCode.isBlank();
    }

    private static int calculateDiscountAmount(int currentPrice, String couponCode) {
        if (WELCOME_CGV.equals(couponCode)) {
            return WELCOME_DISCOUNT;
        }

        if (VIP_HALF_PRICE.equals(couponCode)) {
            return currentPrice / 2;
        }

        throw new CustomException(ErrorCode.INVALID_COUPON_CODE);
    }
}
