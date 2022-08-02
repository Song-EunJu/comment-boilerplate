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
                .deleteStatus(comment.getDeleteStatus())
                .secret(comment.getSecret())
                .reports(comment.getReports())
                .replies(replies)
                .build();
    }

}

//    public static String getChangedComment(Comment comment, Member member, Boolean option) {
//        Long memberId = member.getId(); // 조회자
//        Long parentCommentWriterId = comment.getParent().getId(); // 부모댓글작성자
//        Long commentWriterId = comment.getMember().getId(); // 댓글작성자
//
//        // 비공개 댓글 바로 위 부모댓글만 = true
//        if (member.getRole() != Role.ADMIN) {  // 관리자 권한 아닌 경우
//            if (comment.getDeleteStatus()) // 삭제 댓글처리
//                return "삭제된 댓글입니다";
//            else { // 비밀 댓글 조회처리
//                if (comment.getSecret()) { // 비댓인 경우
//                    if (comment.getMember().getRole() != Role.GUEST) { // 게스트 유저가 아닌경우
//                        if (commentWriterId != memberId) { // 댓글작성자!=조회자
//                            if (option) { // 최상위 부모 댓글까지 조회 허용
//                                while (true) { // 최상위 부모 댓글이 아닐 때까지
//                                    if (comment.getMember().getId() == memberId) { // 부모댓글 작성자 == 조회자
//                                        return comment.getComment();
//                                    } ele { // 부모댓글 작성자 != 조회자인 경우
//                                        if (comment.getParent() == comment) { // 최상위 댓글까지 왔는데도 return 안됐으니까 비댓
//                                            return "비밀 댓글입니다";
//                                        }
//                                        comment = comment.getParent(); // 다시 한 계층 더 올라감
//                                    }
//                                }
//                            } else { // 바로 위 부모 댓글까지 조회 허용
//                                if (parentCommentWriterId != memberId)  // 부모댓글작성자 != 조회자
//                                    return "비밀 댓글입니다"; // 부모 댓글 작성자!=조회자
//                                else
//                                    return comment.getComment(); // 부모댓글작성자 == 조회자
//                            }
//                        }
//                        return comment.getComment(); // 댓글작성자==조회자인 경우
//                    }
//                    return "비밀 댓글입니다"; // 게스트 유저인 경우
//                }
//                return comment.getComment();  // 애초에 비댓이 아닌 경우
//            }
//        }
//        return comment.getComment(); // 관리자 권한인 경우
//    }
//
//    public static List<CommentResponse> toDtoList(List<Comment> replies, Member member, Boolean option) {
//        return replies
//                .stream()
//                .map(comment -> of(comment, member, option, replies))
//                .collect(Collectors.toList());
//    }
//
//    public static List<ReportResponse> toReportList(List<Report> reports) {
//        return reports
//                .stream()
//                .map(ReportResponse::of)
//                .collect(Collectors.toList());
//    }
//}