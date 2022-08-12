package com.example.commentpractice.dto;

import com.example.commentpractice.entity.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CommentResponse {
    private Long commentId;
    private Long userId;
    private String nickname;
    private String comment; // 댓글
    private Boolean deleteStatus; // 삭제여부
    private Boolean secret;
    private List<CommentResponse> replies;
    private List<ReportResponse> reports;

    public static CommentResponse of(Comment comment, List<CommentResponse> replies, List<ReportResponse> reports) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .userId(comment.getMember().getId())
                .nickname(comment.getMember().getNickname())
                .comment(comment.getComment())
                .deleteStatus(comment.getDeleteStatus())
                .secret(comment.getSecret())
                .reports(reports)
                .replies(replies)
                .build();
    }
}