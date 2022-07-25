package com.example.commentpractice.controller;

import com.example.commentpractice.dto.CommentRequest;
import com.example.commentpractice.dto.CommentUpdateDto;
import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

//    @GetMapping("/comments")
//    public ResponseEntity<List<CommentResponse>> saveComment() {
//        List<CommentResponse> comments = commentService.getComments();
//        return ResponseEntity.created(URI.create("/comments")).body(comments);
//    }

    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> saveComment() {
        List<Comment> comments = commentService.getComments();
        return ResponseEntity.ok().body(comments);
    }

    // 댓글 등록
    @PostMapping("/comment")
    public ResponseEntity<Long> saveComment(@RequestBody CommentRequest commentRequest) {
        Long id = commentService.saveComment(commentRequest);
        return ResponseEntity.created(URI.create("/comment")).body(id);
    }

    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Long> updateComment(
            @RequestBody CommentUpdateDto commentUpdateDto,
            @PathVariable("commentId") Long commentId) {
        commentService.updateComment(commentUpdateDto, commentId);
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    // 대댓글 등록
    @PostMapping("/comments/{commentId}/reply")
    public ResponseEntity<Long> saveReply(
            @RequestBody CommentRequest commentRequest,
            @PathVariable("commentId") Long commentId) {
        Long id = commentService.saveReply(commentRequest, commentId);
        return ResponseEntity.created(URI.create("/comment")).body(id);
    }

    // 대댓글 수정
    @PatchMapping("/comments/{commentId}/reply/{replyId}")
    public ResponseEntity updateReply (
            @RequestBody CommentUpdateDto commentUpdateDto,
            @PathVariable("commentId") Long commentId,
            @PathVariable("replyId") Long replyId) {
        commentService.updateReply(commentUpdateDto, commentId);
        return ResponseEntity.ok().build();
    }

    // 대댓글 삭제
    @DeleteMapping("/comments/{commentId}/reply/{replyId}")
    public ResponseEntity deleteReply (
            @PathVariable("commentId") Long commentId,
            @PathVariable("replyId") Long replyId) {
        commentService.deleteReply(commentId);
        return ResponseEntity.ok().build();
    }
}
