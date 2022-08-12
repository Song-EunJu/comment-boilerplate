package com.example.commentpractice.dto;

import com.example.commentpractice.entity.report.Report;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ReportResponse {
    private Long reportId;
    private String reason;

    public static ReportResponse of(Report report){
        return ReportResponse.builder()
                .reportId(report.getId())
                .reason(report.getReason().name())
                .build();
    }

    public static List<ReportResponse> toReportList(List<Report> reports) {
        return reports
                .stream()
                .map(ReportResponse::of)
                .collect(Collectors.toList());
    }
}
