package com.example.commentpractice.dto;

import com.example.commentpractice.entity.comment.Comment;
import lombok.Getter;

@Getter
public class CommentRequest {
    private String comment;
    private Long userId;

    public Comment toEntity() {
        return Comment.builder()
                .comment(comment)
                .build();
    }
}
