package com.example.commentpractice.service;

import com.example.commentpractice.dto.CommentDeleteDto;
import com.example.commentpractice.dto.CommentReportDto;
import com.example.commentpractice.dto.CommentRequest;
import com.example.commentpractice.dto.CommentResponse;
import com.example.commentpractice.entity.Role;
import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.entity.report.Report;
import com.example.commentpractice.entity.user.Member;
import com.example.commentpractice.repository.CommentRepository;
import com.example.commentpractice.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final MemberService memberService;

    // 댓글 수정 삭제 시 권한 확인 메소드
    public void confirmUpdateAuth(Comment comment, Member member) {
        if (comment.getMember().getId() != member.getId()) // 작성자가 아닌 경우
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "수정 권한이 없습니다");
    }

    // 댓글 삭제 시 권한 확인 메소드
    public void confirmDeleteAuth(Comment comment, Member member) {
        if (comment.getMember().getId() != member.getId() && member.getRole() != Role.ADMIN) // 작성자, 관리자가 아닌 경우
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "삭제 권한이 없습니다");
    }

    // 댓글존재여부 확인
    public Comment findByCommentId(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당번호의 댓글이 없음");
        });
    }

    // 대댓 존재여부 확인
    public Comment findByReplyId(Long replyId){
        return commentRepository.findById(replyId).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당번호의 대댓글이 없음");
        });
    }

    // 댓글 조회
    public List<CommentResponse> getComments(Long userId) {
        Member member = memberService.findById(userId);
        List<CommentResponse> comments = commentRepository
                .findAll()
                .stream()
                .filter(comment -> comment.getDepth() == 0L)
                .map(comment -> CommentResponse.of(comment, member))
                .collect(Collectors.toList());

        return comments;
    }

    // 댓글 등록
    public Long saveComment(CommentRequest commentRequest) {
       Comment comment = commentRequest.toEntity();
       if(commentRequest.getUserId() == 0){ // 가입하지 않은 경우
            Member member = memberService.saveGuest(commentRequest);
            comment.setMember(member);
       }
       else {
           Member member = memberService.findById(commentRequest.getUserId());
           comment.setMember(member);
       }
        return commentRepository.save(comment).getId();
    }

    // 댓글 수정
    public void updateComment(CommentRequest commentRequest, Long commentId) {
        String comments = commentRequest.getComment();
        Member member = memberService.findById(commentRequest.getUserId());
        Comment comment = this.findByCommentId(commentId);

        confirmUpdateAuth(comment, member);
        comment.updateComment(comments);
        commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(CommentDeleteDto commentDeleteDto, Long commentId) {
        Comment comment = this.findByCommentId(commentId);
        Member member = memberService.findById(commentDeleteDto.getUserId());

        confirmDeleteAuth(comment, member); // 댓글작성자와 로그인한 사용자가 같으면
        comment.updateDeleteStatus();
        commentRepository.save(comment);
    }

    // 댓글 신고
    public void reportComment(CommentReportDto commentReportDto, Long commentId){
        String reason = commentReportDto.getReason();
        Member member = memberService.findById(commentReportDto.getUserId());
        Comment comment = this.findByCommentId(commentId);
        System.out.println("comment.getComment() = " + comment.getComment());
        Report report = commentReportDto.toEntity(reason, member, comment);
        System.out.println("report.getReason().name() = " + report.getReason().name());
        Report savedReport = reportRepository.save(report);
        comment.addReport(savedReport);
        commentRepository.save(comment);
    }

    // 대댓글 등록
    public Long saveReply(CommentRequest commentRequest, Long commentId) {
        Comment reply = commentRequest.toEntity();
        Member member = memberService.findById(commentRequest.getUserId());

        Comment comment = this.findByCommentId(commentId);
        reply.setParentAndDepth(commentId, comment.getDepth()+1);
        reply.setMember(member);
        comment.addReply(reply);

        return commentRepository.save(reply).getId();
    }

    // 대댓글 수정
    public void updateReply(CommentRequest commentRequest, Long commentId, Long replyId) {
        String updatedComment = commentRequest.getComment();

        Comment comment = this.findByCommentId(commentId);
        Comment reply = this.findByReplyId(replyId);
        comment.updateComment(updatedComment);
        commentRepository.save(reply);
    }

    // 대댓글 삭제
    public void deleteReply(Long commentId, Long replyId) {
        this.findByCommentId(commentId);
        Comment reply = this.findByReplyId(replyId);
        reply.updateDeleteStatus();
        commentRepository.save(reply);
    }
}
