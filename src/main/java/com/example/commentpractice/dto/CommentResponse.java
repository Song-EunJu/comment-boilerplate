package com.example.commentpractice.dto;

import com.example.commentpractice.entity.Role;
import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.entity.report.Report;
import com.example.commentpractice.entity.user.Member;
import com.example.commentpractice.repository.CommentRepository;
import lombok.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class CommentResponse {
//    @Autowired
    private final CommentRepository commentRepository;

    private Long commentId;
    private Long userId;
    private String nickname;
    private String comment; // 댓글
    private Boolean deleteStatus; // 삭제여부
    private Boolean secret;
    private Long depth;
    private Long parent;
    private List<CommentResponse> comments;
    private List<ReportResponse> reports;

    public CommentResponse of(Comment comment, Member member, Optional<Comment> parentComment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .userId(comment.getMember().getId())
                .nickname(comment.getMember().getNickname())
                .deleteStatus(comment.getDeleteStatus())
                .secret(comment.getSecret())
                .depth(comment.getDepth())
                .parent(comment.getParent())
                .comment(this.getChangedComment(comment, member, parentComment)) // 여기서 내용을 바꾸는 건데
                .comments(toDtoList(comment.getReplies(), member, parentComment))
                .reports(toReportList(comment.getReports()))
                .build();
    }


    public String getChangedComment(Comment comment, Member member, Optional<Comment> parentComment) {
        Long memberId = member.getId(); // 조회자
        Comment parent = commentRepository.findById(comment.getParent()).orElse(null);
        System.out.println("parent = " + parent);
        if (parentComment.isEmpty()) {
            parent = comment;
            if(comment.getId()==3) {
                System.out.println("ㅋㅋ");
                System.out.println(comment);
                System.out.println("parentComment.get() = " + parentComment.get());
            }
        } else
            parent = parentComment.get();

        Long parentCommentWriterId = parent.getMember().getId(); // 부모댓글작성자
        Long commentWriterId = comment.getMember().getId(); // 댓글작성자


        if (member.getRole() != Role.ADMIN) { // 관리자 권한 아닌 경우
            if (comment.getDeleteStatus())
                return "삭제된 댓글입니다";
            else {
                if (comment.getSecret()) { // 비댓인 경우
                    if (comment.getMember().getRole() != Role.GUEST) { // 게스트 유저가 아닌경우
                        if (commentWriterId != memberId) { // 댓글작성자!=조회자
                            if (parentCommentWriterId != memberId) { // 부모댓글작성자 != 조회자
//                                return "비밀 댓글입니다;
                                if(comment.getId() == 3) {
                                    System.out.println("조회자 id :" + memberId);
                                    System.out.println("부모댓글작성자 id : " + parentCommentWriterId);
                                    System.out.println("댓글작성자 id : " + commentWriterId);
                                    System.out.println("------------------------");
                                }
                                return "********************0";
                            }// 부모 댓글 작성자!=조회자
                            else
//                                return comment.getComment();
                                return "********************1"; // 부모댓글작성자 == 조회자
                        }
//                        return comment.getComment();
                        return "********************2"; // 댓글작성자==조회자인 경우
                    }
//                    return "비밀 댓글입니다";
                    return "********************3"; // 게스트 유저인 경우
                }
//                return comment.getComment();
                return "********************4"; // 애초에 비댓이 아닌 경우

            }
        }
//        return comment.getComment(); // 관리자 권한인 경우
        return "********************5"; // 관리자 권한인 경우

    }

    public List<CommentResponse> toDtoList(List<Comment> replies, Member member, Optional<Comment> parentComment) {
        return replies
                .stream()
                .map(comment -> of(comment, member, parentComment))
                .collect(Collectors.toList());
    }

    public static List<ReportResponse> toReportList(List<Report> reports) {
        return reports
                .stream()
                .map(ReportResponse::of)
                .collect(Collectors.toList());
    }
}