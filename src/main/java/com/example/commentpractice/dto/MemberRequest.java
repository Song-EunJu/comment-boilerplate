package com.example.commentpractice.dto;

import com.example.commentpractice.entity.Role;
import com.example.commentpractice.entity.user.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequest {
    private String email;
    private String password;
    private String nickname;

    public Member toEntity(String encryptedPwd){
        return Member.builder()
                .email(email)
                .password(encryptedPwd)
                .nickname(nickname)
                .role(Role.USER)
                .build();
    }
}
