package com.example.commentpractice.controller;

import com.example.commentpractice.dto.MemberRequest;
import com.example.commentpractice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.net.URISyntaxException;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/user")
    public ResponseEntity<Long> saveUser(@RequestBody MemberRequest userDto) throws URISyntaxException {
        Long id = memberService.saveMember(userDto);
        return ResponseEntity.created(new URI("/user")).body(id);
    }
}
