package com.example.commentpractice.dto;

import com.example.commentpractice.entity.comment.Comment;
import lombok.Getter;

@Getter
public class CommentRequest {
    private String comment;
    private Long userId;
    private String nickname;
    private String password;
    private Boolean secret; // 비댓 여부

    public Comment toEntity() {
        return Comment.builder()
                .comment(comment)
                .secret(secret)
                .build();
    }
}
