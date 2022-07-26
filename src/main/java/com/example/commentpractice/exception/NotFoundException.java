package com.example.commentpractice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "존재하지 않음")
public class NotFoundException extends RuntimeException {
}