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
    private List<Comment> allComment = commentRepository.findAll();
    private List<CommentReply> allCommentReply = commentReplyRepository.findAll();

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

    // 댓글 존재 여부 확인
    public Comment findById(Long id){
        return allComment
                .stream()
                .filter(comment -> comment.getId() == id)
                .findFirst()
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 댓글 번호가 없습니다");
                });
    }

    // 해당 댓글을 부모댓글로 가지고 있는 대댓글이 있는지 확인
    public List<CommentReply> findByParentId(Long id){
        return allCommentReply
                .stream()
                .filter(commentReply -> commentReply.getParentId() == id)
                .collect(Collectors.toList());
    }

    // 해당 댓글이 CommentReply 에 있는지 확인
    public CommentReply findByCommentId(Long id){
        return allCommentReply
                .stream()
                .filter(commentReply -> commentReply.getCommentId() == id)
                .findFirst()
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 댓글 번호가 없습니다");
                });
    }

    public List<CommentResponse> getComments(Long userId, Boolean option) {
        Member member = memberService.findById(userId); // 조회하려는 사람

        List<CommentReply> commentReply = allCommentReply
                .stream()
                .filter(cp -> cp.getParent() == true) // 부모 댓글인 애들로필터링해서
                .collect(Collectors.toList());

        List<CommentResponse> finalList = new ArrayList<>(); // 최종 리턴할 리스트

        for(int i=0;i<commentReply.size();i++) {
            CommentReply cr = commentReply.get(i);
            Comment comment = findById(cr.getCommentId());

            List<CommentResponse> list = getReplies(comment, member, option); // 1번 댓글의 대댓글 목록들을 가져오기 위함.
            finalList.add(CommentResponse.of(comment, list)); // 1번 객체에 딸려오는 놈들
        }
        return finalList;
    }


    public List<CommentResponse> getReplies(Comment parent, Member member, Boolean option){
        List<CommentResponse> list = new ArrayList<>();
        List<CommentReply> commentReplies = findByParentId(parent.getId()); // 1번 댓글을 부모로 갖는 자식댓글리스트

        if (commentReplies.isEmpty()) { // 대댓글의 끝까지 온 경우 replies 에 아무것도 없는 것 리턴
            return new ArrayList<>();
        }

        for(int i=0;i<commentReplies.size();i++) {
            CommentReply commentReply = commentReplies.get(i);
            Comment reply = findById(commentReply.getCommentId());

            List<CommentResponse> response = getReplies(reply, member, option);
            CommentResponse commentResponse = CommentResponse.of(reply, response);
            commentResponse.setComment(changeComment(reply, member, option));
            list.add(commentResponse);
        }
        return list;
    }

    public String changeComment(Comment comment, Member member, Boolean option) {
        Long memberId = member.getId(); // 조회자
        Long commentWriterId = comment.getMember().getId(); // 댓글 작성자

        Long parentCommentWriterId; // 부모 댓글 작성자
        CommentReply commentReply = findByCommentId(comment.getId());
        if(commentReply.getParentId() == 0){ // parentId 가 0인 경우 부모댓글작성자는 댓글 작성자와 같음
            parentCommentWriterId = comment.getMember().getId();
        }
        else{
            Comment parent = findById(commentReply.getParentId());
            parentCommentWriterId = parent.getMember().getId();
        }

        if (member.getRole() != Role.ADMIN) {  // 관리자 권한 아닌 경우
            if (comment.getDeleteStatus()) // 삭제 댓글처리
                return "삭제된 댓글입니다";
            else { // 비밀 댓글 조회처리
                if (comment.getSecret()) { // 비댓인 경우
                    if (comment.getMember().getRole() != Role.GUEST) { // 게스트 유저가 아닌경우
                        if (commentWriterId != memberId) { // 댓글작성자!=조회자
                            if (option) { // 최상위 부모 댓글까지 조회 허용
                                Comment next = comment;
                                while (true) { // 최상위 부모 댓글이 아닐 때까지
                                    if (next.getMember().getId() == memberId) { // 부모댓글 작성자 == 조회자
                                        return comment.getComment();
                                    } else { // 부모댓글 작성자 != 조회자인 경우
                                        CommentReply cr = findByCommentId(next.getId());
                                        if(cr.getParentId() == 0)
                                            return "비밀 댓글입니다";
                                        next = findById(cr.getParentId()); // 다시 한 계층 더 올라감
                                    }
                                }
                            } else { // 바로 위 부모 댓글까지 조회 허용
                                if (parentCommentWriterId != memberId)  // 부모댓글작성자 != 조회자
                                    return "비밀 댓글입니다"; // 부모 댓글 작성자!=조회자
                                else
                                    return comment.getComment(); // 부모댓글작성자 == 조회자
                            }

                        }
                        return comment.getComment(); // 댓글작성자==조회자인 경우
                    }
                    return "비밀 댓글입니다"; // 게스트 유저인 경우 비댓은 패스워드 입력해야 볼 수 있음
                }
                return comment.getComment();  // 애초에 비댓이 아닌 경우
            }
        }
        return comment.getComment(); // 관리자 권한인 경우 다 볼 수 있음
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
        Comment comment = findById(commentId);

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
        Comment comment = findById(commentId);

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
        Comment comment = findById(commentId);
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

        CommentReply commentReply = new CommentReply(savedReply.getId(), commentId, false);
        commentReplyRepository.save(commentReply);

        return savedReply.getId();
    }
}
