package com.example.commentpractice.entity.report;

import com.example.commentpractice.entity.BaseTimeEntity;
import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.entity.user.Member;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @JsonManagedReference
    @ManyToOne(targetEntity = Member.class)
    private Member member;

    @JsonManagedReference
    @ManyToOne(targetEntity = Comment.class)
    private Comment comment;
}
