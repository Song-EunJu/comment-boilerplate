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
    private Long depth;
    private List<CommentResponse> comments;
    private List<ReportResponse> reports;

    public static CommentResponse of(Comment comment, Member member, Comment parentComment){
        return CommentResponse.builder()
                .commentId(comment.getId())
                .userId(comment.getMember().getId())
                .nickname(comment.getMember().getNickname())
                .deleteStatus(comment.getDeleteStatus())
                .secret(comment.getSecret())
                .depth(comment.getDepth())
                .comment(getChangedComment(comment, member, parentComment))
                .comments(toDtoList(comment.getReplies(), member, parentComment))
                .reports(toReportList(comment.getReports()))
                .build();
    }

    public static String getChangedComment(Comment comment, Member member, Comment parentComment){
        Long memberId = member.getId();
        Long parentCommentWriterId = parentComment.getMember().getId();
        Long commentWriterId = comment.getMember().getId();
        if(member.getRole() != Role.ADMIN) { // 관리자 권한 아닌 경우
            if (comment.getDeleteStatus())
                return "삭제된 댓글입니다";
            else if (comment.getSecret() && (comment.getMember().getRole() == Role.GUEST ||
                    (commentWriterId != memberId || parentCommentWriterId != memberId))){
                System.out.println("--------------------------------------------");
                System.out.println("commentWriterId+\" \"+parentCommentWriterId = " + commentWriterId+" "+parentCommentWriterId);
                System.out.println("comment.getSecret() = " + comment.getSecret());
                System.out.println("comment.getMember().getRole() == Role.GUEST = " + (comment.getMember().getRole() == Role.GUEST));
                System.out.println("(commentWriterId != memberId = " + (commentWriterId != memberId));
                System.out.println("(parentCommentWriterId != memberId) = " + (parentCommentWriterId != memberId));
                System.out.println("(commentWriterId != memberId && parentCommentWriterId != memberId)) = " + (commentWriterId != memberId && parentCommentWriterId != memberId));
                
                return "비밀 댓글입니다";
            }
            System.out.println("--------------------------------------------");

            System.out.println("비댓아님");
            System.out.println("commentWriterId+\" \"+parentCommentWriterId = " + commentWriterId+" "+parentCommentWriterId);
            System.out.println("comment.getSecret() = " + comment.getSecret());
            System.out.println("comment.getMember().getRole() == Role.GUEST = " + (comment.getMember().getRole() == Role.GUEST));
            System.out.println("(commentWriterId != memberId = " + (commentWriterId != memberId));
            System.out.println("(parentCommentWriterId != memberId) = " + (parentCommentWriterId != memberId));
            System.out.println("(commentWriterId != memberId && parentCommentWriterId != memberId)) = " + (commentWriterId != memberId && parentCommentWriterId != memberId));

        }
        return comment.getComment();
    }
    public static List<CommentResponse> toDtoList(List<Comment> replies, Member member, Comment parentComment){
        return replies
                .stream()
                .map(comment -> CommentResponse.of(comment, member, parentComment))
                .collect(Collectors.toList());
    }

    public static List<ReportResponse> toReportList(List<Report> reports){
        return reports
                .stream()
                .map(ReportResponse::of)
                .collect(Collectors.toList());
    }
}