package com.example.commentpractice.entity.comment;

import com.example.commentpractice.dto.CommentRequest;
import com.example.commentpractice.entity.BaseTimeEntity;
import com.example.commentpractice.entity.report.Report;
import com.example.commentpractice.entity.user.Member;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Table
@DynamicInsert
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment; // 댓글

    @ColumnDefault("false")
    private Boolean secret;

    @OneToOne
    private Comment parent; // 부모댓글이면 자기자신, 대댓이면 부모댓글 comment

    private Long depth;

    @ColumnDefault("false")
    private Boolean deleteStatus; // 삭제여부

    // 부모 -> 자식 ( 역방향)
    @JsonManagedReference
    @ManyToOne(targetEntity = Member.class)
    private Member member;

    @OneToMany(targetEntity = Report.class)
    List<Report> reports = new ArrayList<>();

    @OneToMany(targetEntity = Comment.class)
    List<Comment> replies = new ArrayList<>();

    public void setMember(Member member) {
        this.member = member;
    }

    public void setParentComment(Comment comment) {
        this.parent = comment;
    }

    public void updateDeleteStatus() {
        this.deleteStatus = true;
    }

    public void setParentAndDepth(Comment parent, Long depth) {
        this.parent = parent;
        this.depth = depth;
    }

    public void updateComment(CommentRequest commentRequest) {
        if (commentRequest.getComment() != null)
            this.comment = commentRequest.getComment();
        if (commentRequest.getSecret() != null)
            this.secret = commentRequest.getSecret();
    }

    public void addReply(Comment reply) {
        replies.add(reply);
    }

    public void addReport(Report report) {
        reports.add(report);
    }
}
