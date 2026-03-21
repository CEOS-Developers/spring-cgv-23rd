package com.ceos23.spring_cgv_23rd.Movie.DTO.Response;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Comment;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record CommentWrapperDTO(
        long id, String content

) {
    public static CommentWrapperDTO from(Comment comment){
        return CommentWrapperDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .build();
    }

    public static List<CommentWrapperDTO> from(List<Comment> comment){
        List<CommentWrapperDTO> res = new ArrayList<>();

        for(Comment cmm : comment){
            res.add(CommentWrapperDTO.from(cmm));
        }

        return res;
    }
}
