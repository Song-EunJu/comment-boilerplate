package com.example.commentpractice.entity.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Table
@DynamicInsert
public class CommentReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Comment 테이블과 외래키 연결
    @JoinColumn(foreignKey = @ForeignKey(name = "commentId"))
    private Long commentId;

    @JoinColumn(foreignKey = @ForeignKey(name = "commentId"))
    private Long replyId;

    @Builder
    public CommentReply(Long commentId, Long replyId) {
        this.commentId = commentId;
        this.replyId = replyId;
    }
}
