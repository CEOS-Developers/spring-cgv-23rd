package com.cgv.spring_boot.domain.reservation.entity;

import com.cgv.spring_boot.domain.reservation.exception.ReservationErrorCode;
import com.cgv.spring_boot.domain.theater.entity.HallType;
import com.cgv.spring_boot.global.error.exception.BusinessException;

public record SeatPosition(String seatRow, int seatCol) {

    public SeatPosition {
        String normalizedRow = normalizeSeatRow(seatRow);
        validate(normalizedRow, seatCol);
        seatRow = normalizedRow;
    }

    public void validateAgainst(HallType hallType) {
        char maxRow = (char) ('A' + hallType.getRowCount() - 1);

        if (seatRow.charAt(0) > maxRow || seatCol > hallType.getColCount()) {
            throw new BusinessException(ReservationErrorCode.INVALID_SEAT_POSITION);
        }
    }

    private static String normalizeSeatRow(String seatRow) {
        if (seatRow == null) {
            throw new BusinessException(ReservationErrorCode.INVALID_SEAT_POSITION);
        }

        String normalizedRow = seatRow.trim().toUpperCase();
        if (normalizedRow.length() != 1) {
            throw new BusinessException(ReservationErrorCode.INVALID_SEAT_POSITION);
        }

        return normalizedRow;
    }

    private static void validate(String seatRow, int seatCol) {
        char row = seatRow.charAt(0);

        if (row < 'A' || row > 'Z' || seatCol < 1) {
            throw new BusinessException(ReservationErrorCode.INVALID_SEAT_POSITION);
        }
    }
}
