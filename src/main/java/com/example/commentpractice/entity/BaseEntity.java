package com.example.commentpractice.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseEntity implements Serializable {
    @Column
    @DateTimeFormat
    private LocalDateTime created;

    @Column
    @DateTimeFormat
    private LocalDateTime updated;
}
