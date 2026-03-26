// CinemaLike.java
package com.ceos23.spring_boot.cgv.domain.like;

import com.ceos23.spring_boot.cgv.domain.BaseEntity;
import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.domain.user.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "cinema_like",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_cinema_like",
                        columnNames = {"user_id", "cinema_id"}
                )
        }
)
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "liked_at"))
})
public class CinemaLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    public CinemaLike(User user, Cinema cinema) {
        this.user = user;
        this.cinema = cinema;
    }
}