package com.example.commentpractice.service;

import com.example.commentpractice.dto.CommentRequest;
import com.example.commentpractice.dto.CommentUpdateDto;
import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.entity.user.Member;
import com.example.commentpractice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberService memberService;

    // 전체 댓글 조회
//    public List<CommentResponse> getComments(){
//        return commentRepository
//                .findAll()
//                .stream()
//                .map()
//                .collect(Collectors.toList());
//    }

    public List<Comment> getComments(){
        return commentRepository.findAll();
    }

//    // 사용자별 전체 댓글 조회
//    public List<CommentResponse> getComments(){
//        return commentRepository
//                .findAll()
//                .stream()
//                .map();
//    }

    // 댓글 등록
    public Long saveComment(CommentRequest commentRequest) {
        Comment comment = commentRequest.toEntity();
        Member member = memberService.findById(commentRequest.getUserId());
        comment.setMember(member);
        return commentRepository.save(comment).getId();
    }

    // 댓글 수정
    public void updateComment(CommentUpdateDto commentUpdateDto, Long commentId) {
        String comments = commentUpdateDto.getComment();

        commentRepository.findById(commentId).ifPresentOrElse(
                (comment) -> {
                    comment.updateComment(comments);
                }, () -> {
                    new NoSuchElementException("해당번호의 댓글이 없음");
                }
        );
        // 저장해야 값이 바뀌던디
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        commentRepository.findById(commentId).ifPresentOrElse(comment -> {
            comment.updateDeleteStatus();
        }, () -> {
            new NoSuchElementException("해당번호의 댓글이 없음");
        });
    }

    // 대댓글 등록
    public Long saveReply(CommentRequest commentRequest, Long commentId) {
        Comment reply = commentRequest.toEntity();
        Member member = memberService.findById(commentRequest.getUserId());

        // 부모댓글 세팅하는 작업
        commentRepository.findById(commentId).ifPresent(comment -> {
            comment.setParentComment(commentId);
        });

        reply.setMember(member);
        return commentRepository.save(reply).getId();
    }

    // 대댓글 수정
    public void updateReply(CommentUpdateDto commentUpdateDto, Long commentId) {
        String reply = commentUpdateDto.getComment();

        commentRepository.findById(commentId).ifPresentOrElse(
                (instance) -> {
                    instance.updateComment(reply);
                }, () -> {
                    new NoSuchElementException("해당번호의 댓글이 없음");
                }
        );
        // 저장해야 값이 바뀌던디
    }

    // 대댓글 삭제
    public void deleteReply(Long commentId) {
        commentRepository.findById(commentId).ifPresentOrElse(comment -> {
            comment.updateDeleteStatus();
        }, () -> {
            new NoSuchElementException("해당번호의 대댓글이 없음");
        });
    }
}
