package cgv_23rd.ceos.domain.movie;

import cgv_23rd.ceos.domain.Theater.Screen;
import cgv_23rd.ceos.domain.reservation.Reservation;
import cgv_23rd.ceos.domain.reservation.ReservationSeat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MovieScreen {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    private Integer sequence;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @OneToMany(mappedBy = "movieScreen")
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "movieScreen")
    private List<ReservationSeat> reservationSeats = new ArrayList<>();
}
