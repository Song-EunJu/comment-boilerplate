package com.example.commentpractice.service;

import com.example.commentpractice.dto.MemberRequest;
import com.example.commentpractice.entity.user.Member;
import com.example.commentpractice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;
    public Long saveMember(MemberRequest userDto) {
        String encryptedPwd = passwordEncoder.encode(userDto.getPassword());
        Member member = userDto.toEntity(encryptedPwd);
        return memberRepository.save(member).getId(); // member entity 에 getter 가 없어서 계속 안떴음
    }

    public Member findById(Long id){
        return memberRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("해당하는 유저 번호가 없습니다");
        });
    }
}
