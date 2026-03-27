package com.ceos23.spring_cgv_23rd.Reservation.DTO.Response;

import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationType;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponseDTO(
        ReservationType type,
        long id,
        long theaterId,
        long movieId, String movieName,
        LocalDateTime reservationTime,
        long price,
        List<ReservationSeatWrapperDTO> rss
) {
    public ReservationResponseDTO(ReservationType type){
        this(type, 0, 0, 0, null, null, 0, null);
    }

    public static ReservationResponseDTO createForReserve(Reservation res){
        return new ReservationResponseDTO(
                ReservationType.RESERVATION,
                res.getId(),
                res.getScreening().getScreen().getTheater().getId(),
                res.getScreening().getMovie().getId(),
                res.getScreening().getMovie().getMovieName(),
                res.getReservationDate(),
                res.getTotalPrice(),
                ReservationSeatWrapperDTO.create(res.getReservationSeats())
        );
    }

    public static ReservationResponseDTO createForDelete(){
        return new ReservationResponseDTO(ReservationType.WITHDRAW);
    }
}
//@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name = "screening_id")
//    private Screening screening;
//
//    @OneToMany(mappedBy = "reservation")
//    private List<ReservationSeat> reservationSeats = new ArrayList<>();
//
//    private LocalDateTime reservationDate;
//
//    private int totalPrice;