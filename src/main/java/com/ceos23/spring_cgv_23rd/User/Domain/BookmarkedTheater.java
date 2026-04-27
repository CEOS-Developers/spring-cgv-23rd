package com.ceos23.spring_cgv_23rd.User.Domain;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.print.Book;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkedTheater {
    private BookmarkedTheater(Theater theater, User user){
        this.theater = theater;
        this.user = user;
    }

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public static BookmarkedTheater create(Theater th, User user){
        return new BookmarkedTheater(
                th, user
        );
    }
}
