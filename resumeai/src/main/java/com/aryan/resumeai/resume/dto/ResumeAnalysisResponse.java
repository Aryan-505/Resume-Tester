package com.aryan.resumeai.resume.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalysisResponse {
    
    @JsonProperty("ats_score")
    private Double atsScore;

    @JsonProperty("skills_extracted")
    private List<String> skillsExtracted;

    @JsonProperty("missing_skills")
    private List<String> missingSkills;

    private List<String> suggestions;
}