package com.example.commentpractice.repository;

import com.example.commentpractice.entity.comment.CommentReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReplyRepository extends JpaRepository<CommentReply, Long> {
//    List<CommentReply> findAllOrOrderByParentId();
}