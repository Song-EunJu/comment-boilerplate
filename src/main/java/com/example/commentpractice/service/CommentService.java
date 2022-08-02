package com.example.commentpractice.service;

import com.example.commentpractice.dto.CommentDeleteDto;
import com.example.commentpractice.dto.CommentReportDto;
import com.example.commentpractice.dto.CommentRequest;
import com.example.commentpractice.dto.CommentResponse;
import com.example.commentpractice.entity.comment.Comment;
import com.example.commentpractice.entity.comment.CommentReply;
import com.example.commentpractice.entity.report.Report;
import com.example.commentpractice.entity.user.Member;
import com.example.commentpractice.entity.user.Role;
import com.example.commentpractice.repository.CommentReplyRepository;
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
    private final CommentReplyRepository commentReplyRepository;

    // 댓글 수정 삭제 시 권한 확인 메소드
    public Comment findById(Long id){
        return commentRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 댓글 번호가 없습니다");
        });
    }

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

//    public List<CommentResponse> getComments(Long userId, Boolean allParent) {
//        Member member = memberService.findById(userId); // 조회하려는 사람
//
//        /*
//            findAll() 까지는 문제가 없음.
//            근데 댓글의 대댓글을 계속 select 해오는 것이 문제 -> jpa n+1 문제 (따라서 fetch join을 써야 함)
//            따라서 서비스 단에서 계층구조를 짜라는 말
//        */
//        return commentRepository
//                .findAll()
//                .stream()
//                .map(comment -> CommentResponse.of(comment, member, allParent))
//                .collect(Collectors.toList());
//    }

    public List<CommentResponse> getComments(Long userId, Boolean allParent) {
        Member member = memberService.findById(userId); // 조회하려는 사람
        List<CommentReply> commentReply = commentReplyRepository
                .findAll()
                .stream()
                .filter(cp -> cp.getParent() == true) // 부모 댓글인 애들로필터링해서
                .collect(Collectors.toList());

        List<CommentResponse> finalList = new ArrayList<>(); // 이거 고대로 리턴할거임

        for (CommentReply cr : commentReply) { // 얘네가 finalList.add(commentResponse) 바로 아래에 들어가는 댓글
            Comment comment = commentRepository.findById(cr.getCommentId()).get();// 1번 댓글
            getReplies(comment, cr); // 1번 댓글의 대댓글 목록들을 가져오기 위함.
            // 1번 댓글의 대댓글 목록들로 점점 파고들면서 대댓글이 업을 때까지 돌림

//            finalList.add(CommentResponse.of(comment, ));
            // 여기서 1번 댓글에  대댓글까지 딸린 comment 하나를 commentResponse로 만들어서 추가
            // 대댓글 목록 다 추가한 후 최종적으로 finalList에 add
        }
        return finalList;
    }

    public List<CommentResponse> getReplies(Comment parent, CommentReply cr){

        while (true) { // 해당 commentId 와 같은 것이 parentId 에 없을 때까지 돈다
            List<CommentResponse> list = new ArrayList<>();
            Comment comment = commentRepository.findById(cr.getCommentId()).get(); // 1번 댓글
            List<CommentReply> crs = commentReplyRepository.findByParentId(comment.getId()); // 1번댓글을 부모로 갖는 자식댓글리스트
            // 리스트에 값이 없는데 꺼낼수잇나/???

            for(CommentReply commentReply : crs) {
                if (commentReply == null) { // 하나 대댓글의 끝가지 온 것
                    List<CommentResponse> commentResponses = new ArrayList<>();
                    list.add(CommentResponse.of(parent, new ArrayList<>()));
                    return commentResponses;
                }
                List<CommentResponse> response = getReplies(comment, commentReply);
//                list.add(CommentResponse.of(comment, response));
                list.get(0).getReplies().add(CommentResponse.of(comment, response)); //???

            }
        }
    }

    // 댓글 등록
    public Long saveComment(CommentRequest commentRequest) {
       Comment comment = commentRequest.toEntity();
       if(commentRequest.getUserId() == null){ // 가입하지 않고 익명댓글 다는 경우
            Member member = memberService.saveGuest(commentRequest);
            comment.setMember(member);
       }
       else {
           Member member = memberService.findById(commentRequest.getUserId());
           comment.setMember(member);
       }
        Comment savedComment = commentRepository.save(comment);
        CommentReply commentReply = new CommentReply(savedComment.getId(), 0L, true);
        commentReplyRepository.save(commentReply);

        return commentRepository.save(savedComment).getId();
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
        commentRepository.save(comment); // select, update 각각 1번씩 일어남
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
        Member member;
        if(commentRequest.getUserId() == null) // 가입하지 않은 경우
            member = memberService.saveGuest(commentRequest);
        else
            member = memberService.findById(commentRequest.getUserId());

        reply.setMember(member);
        Comment savedReply = commentRepository.save(reply);

        // comment_id, reply_id 를 저장하는 것으로 변경
        CommentReply commentReply = new CommentReply(commentId, savedReply.getId(), false);
        commentReplyRepository.save(commentReply);

        return savedReply.getId();
    }
}
