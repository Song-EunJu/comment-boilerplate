package com.example.commentpractice.repository;

import com.example.commentpractice.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "select c from Comment c left join fetch c.reports")
    List<Comment> findAllComments();
    // join fetch 햇을 때 reports 가 잇는 애들만 뜸
}
