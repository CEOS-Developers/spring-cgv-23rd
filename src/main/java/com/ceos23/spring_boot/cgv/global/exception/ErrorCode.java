package com.ceos23.spring_boot.cgv.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "Invalid request."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "Resource not found."),
    CONFLICT(HttpStatus.CONFLICT, "COMMON_409", "The request conflicts with the current state."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "Internal server error."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "User not found."),
    CINEMA_NOT_FOUND(HttpStatus.NOT_FOUND, "CINEMA_404", "Cinema not found."),
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "MOVIE_404", "Movie not found."),
    CINEMA_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "CINEMA_LIKE_404", "Cinema like not found."),
    MOVIE_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "MOVIE_LIKE_404", "Movie like not found."),
    SCREENING_NOT_FOUND(HttpStatus.NOT_FOUND, "SCREENING_404", "Screening not found."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_404", "Reservation not found."),
    SEAT_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "SEAT_TEMPLATE_404", "Seat template not found."),
    STORE_MENU_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_STOCK_404", "Store menu stock not found."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_404", "Payment not found."),

    EMPTY_SEAT_REQUEST(HttpStatus.BAD_REQUEST, "RESERVATION_400", "No seats were requested."),
    DUPLICATE_SEAT_REQUEST(HttpStatus.BAD_REQUEST, "RESERVATION_401", "Duplicate seats were requested."),
    INVALID_SEAT_FOR_SCREENING(HttpStatus.BAD_REQUEST, "RESERVATION_402", "Seat does not belong to the screening."),
    ALREADY_LIKED_CINEMA(HttpStatus.CONFLICT, "CINEMA_LIKE_409", "Cinema already liked."),
    ALREADY_LIKED_MOVIE(HttpStatus.CONFLICT, "MOVIE_LIKE_409", "Movie already liked."),
    ALREADY_RESERVED_SEAT(HttpStatus.CONFLICT, "RESERVATION_409", "Seat is already reserved."),
    ALREADY_CANCELED_RESERVATION(HttpStatus.CONFLICT, "RESERVATION_410", "Reservation is already canceled."),
    ALREADY_CONFIRMED_RESERVATION(HttpStatus.CONFLICT, "RESERVATION_411", "Reservation is already confirmed."),
    PAYMENT_WINDOW_EXPIRED(HttpStatus.CONFLICT, "RESERVATION_412", "Payment window has expired."),
    DUPLICATE_PAYMENT_ID(HttpStatus.CONFLICT, "PAYMENT_409", "Payment id is already in use."),
    PAYMENT_NOT_CANCELLABLE(HttpStatus.CONFLICT, "PAYMENT_410", "Payment cannot be canceled in its current state."),
    PAYMENT_NOT_COMPLETABLE(HttpStatus.CONFLICT, "PAYMENT_411", "Payment cannot be completed in its current state."),
    INSUFFICIENT_MENU_STOCK(HttpStatus.BAD_REQUEST, "STORE_STOCK_400", "Insufficient menu stock.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
