package com.example.commentpractice.service;

import com.example.commentpractice.dto.CommentRequest;
import com.example.commentpractice.dto.MemberRequest;
import com.example.commentpractice.entity.user.Member;
import com.example.commentpractice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.commentpractice.dto.MemberRequest.toGuestEntity;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public List<Member> getMembers(){
        return memberRepository.findAll();
    }

    public Long saveMember(MemberRequest userDto) {
        String role = userDto.getRole();
        String encryptedPwd = passwordEncoder.encode(userDto.getPassword());
        Member member = userDto.toEntity(encryptedPwd, role);
        return memberRepository.save(member).getId();
    }

    public Member saveGuest(CommentRequest.Create commentRequest) {
        String nickname = commentRequest.getNickname();
        String encryptedPwd = passwordEncoder.encode(commentRequest.getPassword());
        Member member = toGuestEntity(encryptedPwd, nickname);
        return memberRepository.save(member);
    }

    public Member findById(Long id){
        System.out.println(id);  // members 안에는 아무것도없구나 !

        /* 원래 이부분
        private List<Member> members;

        @Bean
        public void initMembers(){
            members = memberRepository.findAll();
        }

        이렇게 했었는데 이렇게 되면 멤버를 저장했을 때 members 에는 아무것도 안들어가지게 됨.

        */
        return getMembers()
                .stream()
                .filter(member -> member.getId() == id)
                .findAny()
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 유저 번호가 없습니다");
                });
    }
}
