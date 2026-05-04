package com.ceos23.spring_cgv_23rd.Reservation.DTO.Response;

import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;

import java.util.List;

public record RemainingSeatsDTO(
        long screeningId,
        int totalSeatAmount,
        int leavingSeatAmount,
        List<String> seats
) {
    public static RemainingSeatsDTO create(Screening sc, List<String> ss){
        return new RemainingSeatsDTO(
                sc.getId(),
                sc.getScreen().getSeatAmount(),
                sc.getScreen().getSeatAmount() - ss.size(),
                ss);
    }
}
