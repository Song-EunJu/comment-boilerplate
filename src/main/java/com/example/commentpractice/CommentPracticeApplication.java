package com.example.commentpractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CommentPracticeApplication {
	public static void main(String[] args) {
		SpringApplication.run(CommentPracticeApplication.class, args);
	}

}
