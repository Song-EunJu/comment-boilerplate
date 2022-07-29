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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PasswordEncoder passwordEncoder;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final MemberService memberService;
    private final CommentResponse commentResponse;

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

    // 익명 댓글/수정 삭제 시 비밀번호 확인 메소드
    public void confirmPassword(String password, Comment comment){
        if(!passwordEncoder.matches(password, comment.getMember().getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 맞지 않습니다");
    }

    // 댓글존재여부 확인
    public Comment findByCommentId(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당번호의 댓글이 없음");
        });
    }

    // 댓글 조회 - 바로 위 부모댓글만 자식 비댓 확인할 수 있는 경우
    public List<CommentResponse> getComments(Long userId, Boolean allParent) {
        Member member = memberService.findById(userId); // 조회하려는 사람

        List<CommentResponse> comments;
        if(allParent == false){
            comments = commentRepository
                    .findAll()
                    .stream()
                    .map(comment -> commentResponse.of(comment, member,
                            commentRepository.findById(comment.getParent())))
                    .filter(comment -> comment.getDepth() == 0L)
                    .collect(Collectors.toList());

            return comments;
        }
        else
            return new ArrayList<>();
    }

    public List<Comment> findAll(){
        return commentRepository
                .findAll()
                .stream()
                .filter(comment -> comment.getDepth() == 0L)
                .collect(Collectors.toList());
    }

    // 댓글 등록
    public Long saveComment(CommentRequest commentRequest) {
       Comment comment = commentRequest.toEntity();
       if(commentRequest.getUserId() == null){ // 가입하지 않은 경우
            Member member = memberService.saveGuest(commentRequest);
            comment.setMember(member);
            comment.setParentAndDepth(0L, 0L);
       }
       else {
           Member member = memberService.findById(commentRequest.getUserId());
           comment.setMember(member);
       }
        return commentRepository.save(comment).getId();
    }

    // 댓글 수정
    public void updateComment(CommentRequest commentRequest, Long commentId) {
        Comment comment = this.findByCommentId(commentId);

        if(commentRequest.getPassword() != null) // 익명 댓글 수정하는 경우 비밀번호 확인 - 비밀번호만 입력하면 됨
            confirmPassword(commentRequest.getPassword(), comment);
        else{
            Member member = memberService.findById(commentRequest.getUserId());
            confirmUpdateAuth(comment, member);
        }

        comment.updateComment(commentRequest);
        commentRepository.save(comment);
        // select, update 각각 1번
    }

    // 댓글 삭제
    public void deleteComment(CommentDeleteDto commentDeleteDto, Long commentId) {
        Comment comment = this.findByCommentId(commentId);

        if(commentDeleteDto.getPassword() != null) // 익명 댓글 삭제하는 경우 비밀번호 확인
            confirmPassword(commentDeleteDto.getPassword(), comment);
        else {
            Member member = memberService.findById(commentDeleteDto.getUserId());
            confirmDeleteAuth(comment, member); // 댓글작성자와 로그인한 사용자가 같으면
        }
        comment.updateDeleteStatus();
        commentRepository.save(comment);
    }

    // 댓글 신고
    public void reportComment(CommentReportDto commentReportDto, Long commentId){
        String reason = commentReportDto.getReason();
        Member member = memberService.findById(commentReportDto.getUserId());
        Comment comment = this.findByCommentId(commentId);
        Report report = commentReportDto.toEntity(reason, member, comment);
        Report savedReport = reportRepository.save(report);
        comment.addReport(savedReport);
        commentRepository.save(comment);
    }

    // 대댓글 등록
    public Long saveReply(CommentRequest commentRequest, Long commentId) {
        Comment reply = commentRequest.toEntity();
        if(commentRequest.getUserId() == null) { // 가입하지 않은 경우
            Member member = memberService.saveGuest(commentRequest);
            reply.setMember(member);
        }
        else {
            Member member = memberService.findById(commentRequest.getUserId());
            reply.setMember(member);
        }

        reply.setParentAndDepth(commentId, reply.getDepth()+1);
        Comment comment = this.findByCommentId(commentId);
        comment.addReply(reply);

        return commentRepository.save(reply).getId();
    }
}
