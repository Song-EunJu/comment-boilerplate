package com.example.commentpractice.dto;

import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.entity.report.Report;
import com.example.commentpractice.entity.report.ReportReason;
import com.example.commentpractice.entity.user.Member;
import lombok.Getter;

@Getter
public class CommentReportDto {
    private Long userId; // 신고한 사람
    private String reason;

    public Report toEntity(String reason, Member member, Comment comment){
        return Report.builder()
                .reason(ReportReason.valueOf(reason))
                .member(member)
                .comment(comment)
                .build();
    }
}
