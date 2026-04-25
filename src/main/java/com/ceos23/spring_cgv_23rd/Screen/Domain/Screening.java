package com.ceos23.spring_cgv_23rd.Screen.Domain;

import com.ceos23.spring_cgv_23rd.DiscountPolicy.DiscountPolicyFactory;
import com.ceos23.spring_cgv_23rd.Movie.Domain.AudienceData;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationRequestDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservationResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import com.ceos23.spring_cgv_23rd.Screen.Service.SeatValidator;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Screening {

    private Screening(Screen screen, Movie movie, LocalDateTime stTime){
        this.screen = screen;
        this.movie = movie;
        this.startTime = stTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private LocalDateTime startTime;

    public LocalTime getStartTimeInLocalTime(){
        return startTime.toLocalTime();
    }

    public int getMoviePrice(){
        return movie.getPrice();
    }

    public boolean isMorning(){
        return startTime.toLocalTime().isBefore(LocalTime.of(11,0));
    }

    public boolean isEvening(){
        return startTime.toLocalTime().isAfter(LocalTime.of(22,0));
    }

    public static Screening create(Screen screen, Movie movie, LocalDateTime startTime){
        return new Screening(screen, movie, startTime);
    }

    public LocalTime calculateEndTime(LocalTime time, long runningTime){
        return time.plusMinutes(runningTime);
    }

    public Reservation reserve(User user, ReservationRequestDTO req,
                                          SeatValidator seatValidator,
                                          DiscountPolicyFactory factory){
        seatValidator.checkValidity(this, req.seatInfos());

        return Reservation.create(
                user,
                this,
                req.toReservingSeats(),
                factory.create(this, req.toSeatInfos())
        );
    }
}
