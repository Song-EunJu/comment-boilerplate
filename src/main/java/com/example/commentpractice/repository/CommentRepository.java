package com.example.commentpractice.repository;

import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.entity.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c left join CommentReply cp on c.id = cp.id")
    List<Comment> findAll(Member member);
}
