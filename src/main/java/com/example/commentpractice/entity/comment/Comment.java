package com.example.commentpractice.entity.comment;

import com.example.commentpractice.dto.CommentRequest;
import com.example.commentpractice.entity.BaseTimeEntity;
import com.example.commentpractice.entity.report.Report;
import com.example.commentpractice.entity.user.Member;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(name = "commentId")
    private Long id;

    private String comment; //  댓글

    @ColumnDefault("false")
    private Boolean secret;

    @ColumnDefault("false")
    private Boolean deleteStatus; // 삭제여부

    @JsonManagedReference
    @ManyToOne(targetEntity = Member.class)
    private Member member;

    @JsonBackReference
    @JsonIgnore
    @OneToMany(targetEntity = Report.class)
    private List<Report> reports = new ArrayList<>();

    public void setMember(Member member) {
        this.member = member;
    }

    public void updateDeleteStatus() {
        this.deleteStatus = true;
    }


    public void updateComment(CommentRequest commentRequest) {
        if (commentRequest.getComment() != null)
            this.comment = commentRequest.getComment();
        if (commentRequest.getSecret() != null)
            this.secret = commentRequest.getSecret();
    }

    public void addReport(Report report) {
        reports.add(report);
    }
}
