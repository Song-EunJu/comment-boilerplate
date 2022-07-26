package com.example.commentpractice.dto;

import com.example.commentpractice.entity.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentResponse {
    private Long commentId;
    private Long userId;
    private String nickname;
    private String comment; // 댓글
    private Integer deleteStatus; // 삭제여부
    private Boolean secret;

    public static CommentResponse of(Comment comment){
        return CommentResponse.builder()
                .commentId(comment.getId())
                .userId(comment.getMember().getId())
                .nickname(comment.getMember().getNickname())
                .comment(comment.getComment())
                .deleteStatus(comment.getDeleteStatus())
                .secret(comment.getSecret())
                .build();
    }
}
