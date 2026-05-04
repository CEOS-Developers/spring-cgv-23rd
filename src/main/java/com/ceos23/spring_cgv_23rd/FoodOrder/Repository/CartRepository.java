package com.ceos23.spring_cgv_23rd.FoodOrder.Repository;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Cart;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("""
        select c from Cart c
        left join fetch c.cartItems
        where c.id = :id
    """)
    Optional<Cart> findByIdWithItems(@Param("id") long id);

    @Query("""
    select c from Cart c
    where c.user = :user
    and c.theater = :theater
    and c.status = :status
""")
    Optional<Cart> findByUserAndTheaterAndStatus(
            @Param("user") User user,
            @Param("theater") Theater theater,
            @Param("status") ReservationStatus status
    );

    Optional<Cart> findByUserAndStatus(User user, ReservationStatus status);

    Optional<Cart> findByIdAndStatus(long id, ReservationStatus status);

    boolean findByUser(User user);
}
