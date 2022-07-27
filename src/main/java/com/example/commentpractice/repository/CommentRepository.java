package com.example.commentpractice.repository;

import com.example.commentpractice.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
//    @Query("select c from Comment where c.parent =:id")
//    List<Comment> findByParentId(@Param("id") Long id);
}
