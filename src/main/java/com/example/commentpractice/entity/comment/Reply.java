package com.example.commentpractice.entity.comment;

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
public class Reply extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "replyId")
    private Long id;

    private String reply; // 댓글

    @ColumnDefault("false")
    private Boolean secret;

    @ColumnDefault("false")
    private Boolean deleteStatus; // 삭제여부

    // 부모 -> 자식 ( 역방향)
    @JsonManagedReference
    @ManyToOne(targetEntity = Member.class)
    private Member member;

    @OneToMany(targetEntity = Report.class)
    List<Report> reports = new ArrayList<>();


    public void setMember(Member member) {
        this.member = member;
    }

    public void updateDeleteStatus() {
        this.deleteStatus = true;
    }

    public void addReport(Report report) {
        reports.add(report);
    }
}
