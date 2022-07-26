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

import static com.example.commentpractice.dto.MemberRequest.toGuestEntity;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;
    public Long saveMember(MemberRequest userDto) {
        String role = userDto.getRole();
        String encryptedPwd = passwordEncoder.encode(userDto.getPassword());
        Member member = userDto.toEntity(encryptedPwd, role);
        return memberRepository.save(member).getId();
    }

    public Member saveGuest(CommentRequest commentRequest) {
        String nickname = commentRequest.getNickname();
        String encryptedPwd = passwordEncoder.encode(commentRequest.getPassword());
        Member member = toGuestEntity(encryptedPwd, nickname);
        return memberRepository.save(member);
    }

    public Member findById(Long id){
        return memberRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 유저 번호가 없습니다");
        });
    }
}
