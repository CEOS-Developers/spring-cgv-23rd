package com.ceos23.spring_cgv_23rd.Movie.Domain;

import com.ceos23.spring_cgv_23rd.User.Domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment {
    private Comment(User user, LocalDateTime createdAt, String content){
        this.user = user;
        this.createdAt = createdAt;
        this.content = content;
    }

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Setter
    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Getter
    private LocalDateTime createdAt;

    @Getter
    private String content;

    public void addMovie(Movie movie){
        this.movie = movie;
        movie.getComments().add(this);
    }

    public Comment(User user, Movie movie, String content){
        Comment cmm = new Comment(user, LocalDateTime.now(), content);
        cmm.addMovie(movie);
    }
}
