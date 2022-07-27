package com.example.commentpractice.dto;

import com.example.commentpractice.entity.report.Report;
import lombok.Builder;
import lombok.Getter;

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
}
