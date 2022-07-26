package com.example.commentpractice.repository;

import com.example.commentpractice.entity.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
