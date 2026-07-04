package com.aryan.resumeai.resume.entity;

import com.aryan.resumeai.auth.entity.User;
import com.aryan.resumeai.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore 
    private User user;

    @Column(nullable = false)
    private String originalFileName; // e.g., "Arya_Resume_v2.pdf"

    @Column(nullable = false, unique = true)
    private String storedFileName; // A unique name to prevent overwriting

    @Column(nullable = false)
    private String fileUrl; // The S3 URL or local path to access the file

    private String fileType; // application/pdf or application/vnd.openxmlformats-officedocument.wordprocessingml.document

    private Long fileSize; // In bytes

    private Double atsScore; // To be populated later by your Python AI service

    @ElementCollection
    private List<String> skillsExtracted;

    @ElementCollection
    private List<String> missingSkills;

    @ElementCollection
    @Column(length = 1000)
    private List<String> suggestions;
}