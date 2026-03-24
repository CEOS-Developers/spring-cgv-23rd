package com.ceos23.spring_cgv_23rd.Movie.Repository;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
