package com.example.commentpractice.entity.comment;

import com.example.commentpractice.entity.BaseEntity;
import com.example.commentpractice.entity.user.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Table
@DynamicInsert
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment; // 댓글

    private Boolean secretComment = true; // Boolean default 값 해결하기

    private Long parentCommentId = 0L; // 부모댓글이면 0, 대댓이면 부모댓글 id 를 넣기

    @ColumnDefault("1")
    private int deleteStatus; // default 가 안먹히는데요

    @ManyToOne(targetEntity = Member.class)
    private Member member;

    public void setMember(Member member){
        this.member = member;
    }

    public void setParentComment(Long commentId){
        this.parentCommentId = commentId;
    }

    public void updateDeleteStatus(){
        this.deleteStatus = 1;
    }

    public void updateComment(String comment){
        this.comment = comment;
        this.setCreated(LocalDateTime.now());
    }
}
