package com.example.commentpractice.controller;

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
    public ResponseEntity<List<CommentResponse>> getComments(
            @RequestParam("userId") Long userId,
            @RequestParam("allParent") Boolean allParent) {
        List<CommentResponse> comments = commentService.getComments(userId, allParent);
        return ResponseEntity.ok().body(comments);
    }

    // 댓글 등록
    @PostMapping("/comment")
    public ResponseEntity<Long> saveComment(@RequestBody CommentRequest.Create commentRequest) {
        Long id = commentService.saveComment(commentRequest);
        return ResponseEntity.created(URI.create("/comment")).body(id);
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<String> updateComment(
            @RequestBody CommentRequest.Create commentRequest,
            @PathVariable("commentId") Long commentId) {
        commentService.updateComment(commentRequest, commentId);
        return ResponseEntity.ok().body("댓글 수정 완료");
    }


    // 댓글 신고
    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<String> reportComment(
            @RequestBody CommentRequest.ReportCreate commentReportDto,
            @PathVariable("commentId") Long commentId) {
        commentService.reportComment(commentReportDto, commentId);
        return ResponseEntity.ok().body("댓글 신고 완료");
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity deleteComment(
            @RequestBody CommentRequest.Delete commentDeleteDto,
            @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentDeleteDto, commentId);
        return ResponseEntity.ok().body("댓글 삭제 완료");
    }

    // 대댓글 등록
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Long> saveReply(
            @RequestBody CommentRequest.Create commentRequest,
            @PathVariable("commentId") Long commentId) {
        Long id = commentService.saveReply(commentRequest, commentId);
        return ResponseEntity.created(URI.create("/comment/"+id)).body(id);
    }
}
