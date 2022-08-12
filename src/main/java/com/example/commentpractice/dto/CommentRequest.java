package com.example.commentpractice.dto;

import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.entity.report.Report;
import com.example.commentpractice.entity.report.ReportReason;
import com.example.commentpractice.entity.user.Member;
import lombok.Getter;

public class CommentRequest {

    @Getter
    public static class Create {
        private String comment;
        private Long userId;
        private Boolean secret; // 비댓 여부
        private String nickname;
        private String password;

        public Comment toEntity() {
            return Comment.builder()
                    .comment(comment)
                    .secret(secret)
                    .deleteStatus(false)
                    .build();
        }
//        @Getter
//        public static class AnonymousCreate {
//            private String nickname;
//            private String password;
//        }
    }

    @Getter
    public static class Delete {
        private Long userId;
        private String password;
    }

    @Getter
    public static class ReportCreate {
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
}
