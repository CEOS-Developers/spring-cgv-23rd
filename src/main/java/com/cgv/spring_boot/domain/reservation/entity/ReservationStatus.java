package com.cgv.spring_boot.domain.reservation.entity;

public enum ReservationStatus {
    RESERVED, // 결제 완료(예약 확정)
    CANCELLED, // 사용자 취소
    PENDING_PAYMENT, // 좌석만 잡아둔 상태
    EXPIRED // 결제 시간 초과
}
