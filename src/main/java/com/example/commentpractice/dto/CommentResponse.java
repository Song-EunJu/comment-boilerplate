package com.example.commentpractice.dto;

import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.entity.report.Report;
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
    private List<Report> reports;
    private List<CommentResponse> replies;

    public static CommentResponse of(Comment comment, List<CommentResponse> replies) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .userId(comment.getMember().getId())
                .nickname(comment.getMember().getNickname())
                .comment(comment.getComment())
                .deleteStatus(comment.getDeleteStatus())
                .secret(comment.getSecret())
//                .reports(comment.getReports())
                .replies(replies)
                .build();
    }
}