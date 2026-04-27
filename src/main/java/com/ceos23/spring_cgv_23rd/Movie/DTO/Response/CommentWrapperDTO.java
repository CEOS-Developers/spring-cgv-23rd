package com.ceos23.spring_cgv_23rd.Movie.DTO.Response;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Comment;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record CommentWrapperDTO(
        long id, String content, LocalDateTime createdAt

) {
    public static CommentWrapperDTO create(Comment comment){
        return new CommentWrapperDTO(
                comment.getId(), comment.getContent(), comment.getCreatedAt()
        );
    }

    public static List<CommentWrapperDTO> create(List<Comment> comment){
        List<CommentWrapperDTO> res = new ArrayList<>();

        for(Comment cmm : comment){
            res.add(CommentWrapperDTO.create(cmm));
        }

        return res;
    }
}
