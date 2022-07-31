package com.example.commentpractice.dto;

import com.example.commentpractice.entity.Role;
import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.entity.report.Report;
import com.example.commentpractice.entity.user.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.parameters.P;

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

    public static CommentResponse of(Comment comment, Member member, Boolean option) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .userId(comment.getMember().getId())
                .nickname(comment.getMember().getNickname())
                .deleteStatus(comment.getDeleteStatus())
                .secret(comment.getSecret())
                .depth(comment.getDepth())
                .comment(getChangedComment(comment, member, option)) // 여기서 내용을 바꾸는 건데
                .comments(toDtoList(comment.getReplies(), member, option))
                .reports(toReportList(comment.getReports()))
                .build();
    }


    public static String getChangedComment(Comment comment, Member member, Boolean option) {

        Long memberId = member.getId(); // 조회자
        Long parentCommentWriterId = comment.getParent().getId(); // 부모댓글작성자
        Long commentWriterId = comment.getMember().getId(); // 댓글작성자

        // 비공개 댓글 바로 위 부모댓글만 = true
        if(option) {
            if (member.getRole() != Role.ADMIN) { // 관리자 권한 아닌 경우
                if (comment.getDeleteStatus())
                    return "삭제된 댓글입니다";
                else {
                    if (comment.getSecret()) { // 비댓인 경우
                        if (comment.getMember().getRole() != Role.GUEST) { // 게스트 유저가 아닌경우
                            if (commentWriterId != memberId) { // 댓글작성자!=조회자

                                // 최상위 부모 댓글이 아닐 때까지
                                while(true) {
                                    if (comment.getMember().getId() == memberId) {
                                        return comment.getComment();
                                    } else {
                                        if (comment.getParent() == comment) {
                                            return "비밀 댓글입니다";
                                        }
                                        comment = comment.getParent();
                                    }
                                }
                            }
                            return comment.getComment(); // 댓글작성자==조회자인 경우
                        }
                        return "비밀 댓글입니다"; // 게스트 유저인 경우
                    }
                    return comment.getComment();  // 애초에 비댓이 아닌 경우
                }
            }
            return comment.getComment(); // 관리자 권한인 경우
        }
        else {
            if (member.getRole() != Role.ADMIN) { // 관리자 권한 아닌 경우
                if (comment.getDeleteStatus())
                    return "삭제된 댓글입니다";
                else {
                    if (comment.getSecret()) { // 비댓인 경우
                        if (comment.getMember().getRole() != Role.GUEST) { // 게스트 유저가 아닌경우
                            if (commentWriterId != memberId) { // 댓글작성자!=조회자
                                if (parentCommentWriterId != memberId)  // 부모댓글작성자 != 조회자
                                    return "비밀 댓글입니다"; // 부모 댓글 작성자!=조회자
                                else
                                    return comment.getComment(); // 부모댓글작성자 == 조회자
                            }
                            return comment.getComment(); // 댓글작성자==조회자인 경우
                        }
                        return "비밀 댓글입니다"; // 게스트 유저인 경우
                    }
                    return comment.getComment();  // 애초에 비댓이 아닌 경우
                }
            }
            return comment.getComment(); // 관리자 권한인 경우

        }
    }

    public static List<CommentResponse> toDtoList(List<Comment> replies, Member member, Boolean option) {
        return replies
                .stream()
                .map(comment -> of(comment, member, option))
                .collect(Collectors.toList());
    }

    public static List<ReportResponse> toReportList(List<Report> reports) {
        return reports
                .stream()
                .map(ReportResponse::of)
                .collect(Collectors.toList());
    }
}