package com.example.commentpractice.repository;

import com.example.commentpractice.entity.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
