package com.example.commentpractice.dto;

import com.example.commentpractice.entity.comment.Comment;
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
}
