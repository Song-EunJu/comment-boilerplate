package com.example.commentpractice.dto;

import lombok.Getter;

@Getter
public class ReplyRequest {
    private String reply;
    private Long commentId;
}
