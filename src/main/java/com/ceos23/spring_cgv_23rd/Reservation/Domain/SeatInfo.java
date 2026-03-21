package com.ceos23.spring_cgv_23rd.Reservation.Domain;

public enum SeatInfo {
    ADULT("일반"), CHILD("어린이"), SENIOR("경로");

    private String info;

    SeatInfo(String info){
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
