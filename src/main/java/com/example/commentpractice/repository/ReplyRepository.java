package com.example.commentpractice.repository;

import com.example.commentpractice.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Comment, Long> {
}