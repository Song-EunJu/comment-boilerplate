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
    private Long commentId;

    private Long parentId;

    private Boolean parent;

    @Builder
    public CommentReply(Long commentId, Long parentId, Boolean parentStatus) {
        this.commentId = commentId;
        this.parentId = parentId;
        this.parent = parentStatus;
    }
}
