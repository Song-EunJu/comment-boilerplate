package com.example.commentpractice.repository;

import com.example.commentpractice.entity.comment.CommentReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentReplyRepository extends JpaRepository<CommentReply, Long> {
    List<CommentReply> findByParentId(Long commentId);
    Optional<CommentReply> findByCommentId(Long commentId);
}