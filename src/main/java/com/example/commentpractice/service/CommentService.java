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
import org.springframework.context.annotation.Bean;
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
    private List<CommentReply> filteredCommentsReplies;

    private List<Comment> comments;

    private List<CommentReply> commentReplies;

    @Bean
    public void init(){
        comments = commentRepository.findAll();
        commentReplies = commentReplyRepository.findAll();
        filteredCommentsReplies = new ArrayList<>(commentReplies);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<CommentReply> getCommentReplies() {
        return commentReplies;
    }

    public List<CommentReply> getFilteredCommentReplies(){
        return filteredCommentsReplies;
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
    public void confirmPassword(String password, Comment comment) {
        if(!passwordEncoder.matches(password, comment.getMember().getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 맞지 않습니다");
    }

    // 댓글 존재 여부 확인
    public Comment findCommentById(Long id){
        return getComments()
                .stream()
                .filter(comment -> comment.getId() == id)
                .findFirst()
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 댓글 번호가 없습니다");
                });
    }

    // 해당 댓글을 부모댓글로 가지고 있는 대댓글이 있는지 확인
    public List<CommentReply> findCommentReplyByParentId(Long id){
        /**
         * 최대한 반복문 돌리는 횟수를 줄이기 위해서
         * 1. 일단 부모 번호로 소팅 0,1,2 parentId
         * 2. removeIf 를 하면 또 내장함수에서 while 문으로 전체 원소를 돌기 때문에 얘는 쓰지 않기로
         * 3. for문을 돌면서 부모 댓글일경우에 다 뛰어넘고, 부모댓글보다 커지는 경우 break
         */
        /*
        * 엔티티 하나만 조회할 때는 즉시 로딩으로 설정하면 연관된 팀도 한 쿼리로 가져오도록 최적화 되지만 JPQL을 사용하면 이야기가 달라집니다.
        * JPQL은 연관관계를 즉시로딩으로 설정하는 것과 상관없이 JPQL 자체만으로 SQL로 그대로 번역됩니다.
        * */
        /*
            if (그 빼놓은 리스트에 있으면)
                걔를 꺼내서 반환
            else (만약 리스트에 없으면)
                아래 로직으로 계산필요
         */
        List<CommentReply> finalCommentReplies = new ArrayList<>();
        for(int i=0;i<getFilteredCommentReplies().size();i++){
            CommentReply commentReply = filteredCommentsReplies.get(i);
            if(commentReply.getParentId() == 0) // 부모 댓글일 경우 안돌아도됨
                continue;

            if(commentReply.getParentId() == id) { // 찾는 번호와 같은 경우 리스트에 담음
                finalCommentReplies.add(commentReply);
                filteredCommentsReplies.remove(commentReply); // 찾은 애는 리스트에서 빼버림
            }
            else if(commentReply.getParentId() > id) // 찾는 id 번호보다 부모 번호가 커지는 경우 break
                break;
        }

        return finalCommentReplies;
    }
    
    // 해당 댓글이 CommentReply 에 있는지 확인
    public CommentReply findCommentReplyByCommentId(Long id){
        return getCommentReplies()
                .stream()
                .filter(commentReply -> commentReply.getCommentId() == id)
                .findFirst()
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 댓글 번호가 없습니다");
                });
    }

    public List<CommentResponse> getComments(Long userId, Boolean allParent) {
        Member member = memberService.findById(userId); // 조회하려는 사람

        List<CommentReply> commentReplies = getCommentReplies()
                .stream()
                .filter(cp -> cp.getParentId() == 0)
                .collect(Collectors.toList()); // 최상위 댓글들만 필터링

        getFilteredCommentReplies().sort((o1,o2) -> (int) (o1.getParentId() - o2.getParentId())); // 최상위 댓글들 중 부모댓글로 정렬

        List<CommentResponse> finalList = new ArrayList<>(); // 최종 리턴할 리스트

        return getCommentResponses(member, allParent, finalList, commentReplies);
    }


    private List<CommentResponse> getCommentResponses(Member member, Boolean allParent, List<CommentResponse> list, List<CommentReply> filteredCommentReplies) {
        for(int i=0;i<filteredCommentReplies.size();i++) {
            CommentReply cr = filteredCommentReplies.get(i);
            Comment reply = findCommentById(cr.getCommentId()); // 최상위 댓글 객체 1번

            List<CommentResponse> response = getReplies(reply, member, allParent); // 1번 댓글의 대댓글 받아오기
            CommentResponse commentResponse = CommentResponse.of(reply, response);
            CommentResponse.of(reply, response).setComment(changeComment(reply, member, allParent)); // 반환할 때 문자열 변경
            list.add(commentResponse);
        }
        return list; // 최종 댓글, 대댓글 리스트
    }

    public List<CommentResponse> getReplies(Comment parent, Member member, Boolean allParent){ // 1번 댓글
        List<CommentResponse> list = new ArrayList<>();
        List<CommentReply> commentReplies = findCommentReplyByParentId(parent.getId()); // 1번 댓글을 부모 댓글로 갖는 자식 댓글리스트
        if (commentReplies.isEmpty()) { // 대댓글의 끝까지 온 경우 빈 리스트 리턴
            return new ArrayList<>();
        }
        return getCommentResponses(member, allParent, list, commentReplies);  // commentReplies = 2,5 번
    }

    public String changeComment(Comment comment, Member member, Boolean allParent) {
        Long memberId = member.getId(); // 조회자
        Long commentWriterId = comment.getMember().getId(); // 댓글 작성자
        Long parentCommentWriterId; // 부모 댓글 작성자
        CommentReply cr = findCommentReplyByCommentId(comment.getId());

        if(cr.getParentId() == 0) { // parentId 가 0인 경우 부모댓글작성자는 댓글 작성자와 같음
            parentCommentWriterId = commentWriterId;
        }
        else {
            Comment parent = findCommentById(cr.getParentId());
            parentCommentWriterId = parent.getMember().getId();
        }

        if (member.getRole() == Role.ADMIN) // 관리자인 경우
            return comment.getComment();

        if (comment.getDeleteStatus()) // 삭제 댓글처리
            return "삭제된 댓글입니다";

        if (!comment.getSecret()) // 애초에 비댓이 아닌 경우
            return comment.getComment();

        if (comment.getMember().getRole() == Role.GUEST)
            return "비밀 댓글입니다";

        if (commentWriterId == memberId) // 댓글 작성자 == 조회자
            return comment.getComment();

        return allParent
                ? permitAllParent(comment, memberId)
                : permitDirectParent(comment, parentCommentWriterId, memberId);
    }

    // 모든 상위 부모댓글 조회 가능
    public String permitAllParent(Comment comment, Long memberId) {
        Comment parentComment = comment;

        CommentReply cr = findCommentReplyByCommentId(parentComment.getId()); // 부모댓글의 id

        while (parentComment.getMember().getId() != memberId) {  // 부모댓글 작성자 != 조회자인 동안
            if (cr.getParentId() == 0)
                return "비밀 댓글입니다";
            parentComment = findCommentById(cr.getParentId()); // 다시 한 계층 더 올라감
            cr = findCommentReplyByCommentId(parentComment.getId());
        }
        return comment.getComment(); // while문을 빠져나온 경우 부모댓글 작성자 == 조회자인 경우
    }

    // 바로 상위 댓글 조회 가능
    public String permitDirectParent(Comment comment, Long parentCommentWriterId, Long memberId){
        return parentCommentWriterId != memberId // 부모댓글작성자 != 조회자
                ? "비밀 댓글입니다"
                : comment.getComment();
    }

    // 댓글 등록
    public Long saveComment(CommentRequest commentRequest) {
        Comment comment = commentRequest.toEntity();
        Member member;
        if(commentRequest.getUserId() == null) // 가입하지 않고 익명댓글 다는 경우
            member = memberService.saveGuest(commentRequest);
        else
            member = memberService.findById(commentRequest.getUserId());

        comment.setMember(member);
        Comment savedComment = commentRepository.save(comment);
        CommentReply commentReply = new CommentReply(savedComment.getId(), 0L);
        commentReplyRepository.save(commentReply);

        return commentRepository.save(savedComment).getId();
    }

    // 댓글 수정
    public void updateComment(CommentRequest commentRequest, Long commentId) {
        Comment comment = findCommentById(commentId);

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
        Comment comment = findCommentById(commentId);

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
        Comment comment = findCommentById(commentId);
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

        CommentReply commentReply = new CommentReply(savedReply.getId(), commentId);
        commentReplyRepository.save(commentReply);

        return savedReply.getId();
    }
}
