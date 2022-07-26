package com.example.commentpractice.controller;

import com.example.commentpractice.dto.CommentDeleteDto;
import com.example.commentpractice.dto.CommentReportDto;
import com.example.commentpractice.dto.CommentRequest;
import com.example.commentpractice.dto.CommentResponse;
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

    // 댓글 조회
    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponse>> saveComment(@RequestParam("userId") Long userId) {
        List<CommentResponse> comments = commentService.getComments(userId);
        return ResponseEntity.ok().body(comments);
    }

    // 댓글 등록
    @PostMapping("/comment")
    public ResponseEntity<Long> saveComment(@RequestBody CommentRequest commentRequest) {
        Long id = commentService.saveComment(commentRequest);
        return ResponseEntity.created(URI.create("/comment")).body(id);
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Long> updateComment(
            @RequestBody CommentRequest commentRequest,
            @PathVariable("commentId") Long commentId) {
        commentService.updateComment(commentRequest, commentId);
        return ResponseEntity.ok().build();
    }

    // 댓글 신고
    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<Long> reportComment(
            @RequestBody CommentReportDto commentReportDto,
            @PathVariable("commentId") Long commentId
    ) {
        commentService.reportComment(commentReportDto, commentId);
        return ResponseEntity.ok().build();
    }


    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity deleteComment(
            @RequestBody CommentDeleteDto commentDeleteDto,
            @PathVariable("commentId") Long commentId
    ) {
        commentService.deleteComment(commentDeleteDto, commentId);
        return ResponseEntity.ok().build();
    }

    // 대댓글 등록
    @PostMapping("/comments/{commentId}/reply")
    public ResponseEntity<Long> saveReply(
            @RequestBody CommentRequest commentRequest,
            @PathVariable("commentId") Long commentId
    ) {
        Long id = commentService.saveReply(commentRequest, commentId);
        return ResponseEntity.created(URI.create("/comment")).body(id);
    }

    // 대댓글 수정
    @PutMapping("/comments/{commentId}/replies/{replyId}")
    public ResponseEntity updateReply (
            @RequestBody CommentRequest commentRequest,
            @PathVariable("commentId") Long commentId,
            @PathVariable("replyId") Long replyId) {
        commentService.updateReply(commentRequest, commentId, replyId);
        return ResponseEntity.ok().build();
    }

    // 대댓글 삭제
    @DeleteMapping("/comments/{commentId}/replies/{replyId}")
    public ResponseEntity deleteReply (
            @PathVariable("commentId") Long commentId,
            @PathVariable("replyId") Long replyId) {
        commentService.deleteReply(commentId, replyId);
        return ResponseEntity.ok().build();
    }
}
