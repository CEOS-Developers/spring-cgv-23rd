package com.ceos23.spring_cgv_23rd.Reservation.DTO.Response;

import java.util.List;

public record RemainingSeatsDTO(
        long screeningId,
        List<String> seats
) {
    public static RemainingSeatsDTO create(long id, List<String> ss){
        return new RemainingSeatsDTO(id, ss);
    }
}
