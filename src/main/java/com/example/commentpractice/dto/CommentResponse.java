package com.example.commentpractice.dto;

import com.example.commentpractice.entity.Role;
import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.entity.report.Report;
import com.example.commentpractice.entity.user.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

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
    private List<CommentResponse> comments;
    private List<ReportResponse> reports;

    public static CommentResponse of(Comment comment, Member member){
        return CommentResponse.builder()
                .commentId(comment.getId())
                .userId(comment.getMember().getId())
                .nickname(comment.getMember().getNickname())
                .deleteStatus(comment.getDeleteStatus())
                .secret(comment.getSecret())
                .comment(changeComment(comment, member))
                .comments(toDtoList(comment.getReplies(), member))
                .reports(toReportList(comment.getReports()))
                .build();
    }

    public static String changeComment(Comment comment, Member member){
        if(member.getRole() != Role.ADMIN) { // 관리자 권한 아닌 경우
            if (comment.getDeleteStatus() == true)
                return "삭제된 댓글입니다";
            else if (comment.getSecret() == true && comment.getMember().getId() != member.getId())
                return "비밀 댓글입니다";
        }
        return comment.getComment();
    }

    public static List<CommentResponse> toDtoList(List<Comment> replies, Member member){
        return replies
                .stream()
                .map(comment -> CommentResponse.of(comment, member))
                .collect(Collectors.toList());
    }

    public static List<ReportResponse> toReportList(List<Report> reports){
        return reports
                .stream()
                .map(ReportResponse::of)
                .collect(Collectors.toList());
    }
}