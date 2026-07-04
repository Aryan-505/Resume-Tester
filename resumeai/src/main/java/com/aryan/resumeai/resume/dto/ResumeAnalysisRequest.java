package com.aryan.resumeai.resume.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumeAnalysisRequest {
    private Long resumeId;
    private String fileUrl;
    private String fileType;
}