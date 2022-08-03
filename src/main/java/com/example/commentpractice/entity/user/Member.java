package com.example.commentpractice.entity.user;

import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.entity.report.Report;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonBackReference
    @OneToMany(mappedBy = "member", targetEntity = Comment.class)
    private List<Comment> comments = new ArrayList<>(); // list 보다 set 이 좋다고 했는데 뭐엿지

    @JsonBackReference
    @OneToMany(mappedBy = "member", targetEntity = Report.class)
    private List<Report> reports = new ArrayList<>();
}
