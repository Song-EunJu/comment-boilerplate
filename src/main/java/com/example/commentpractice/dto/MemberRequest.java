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
    private String role;

    public Member toEntity(String encryptedPwd, String role){
        return Member.builder()
                .email(email)
                .password(encryptedPwd)
                .nickname(nickname)
                .role(role == null ? Role.USER : Role.valueOf(role))
                .build();
    }

    public static Member toGuestEntity(String encryptedPwd, String nickname){
        return Member.builder()
                .email(null)
                .password(encryptedPwd)
                .nickname(nickname)
                .role(Role.GUEST)
                .build();
    }
}
