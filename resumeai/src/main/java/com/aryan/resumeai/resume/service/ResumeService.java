package com.aryan.resumeai.resume.service;

import com.aryan.resumeai.auth.entity.User;
import com.aryan.resumeai.auth.repository.UserRepository;
import com.aryan.resumeai.common.exception.ResourceNotFoundException;
import com.aryan.resumeai.resume.dto.ResumeAnalysisRequest;
import com.aryan.resumeai.resume.dto.ResumeAnalysisResponse;
import com.aryan.resumeai.resume.entity.Resume;
import com.aryan.resumeai.resume.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final S3Service fileStorageService; 
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    @Value("${aws.s3.bucket-name:local}")
    private String bucketName;
    
    @Value("${aws.region:local}")
    private String region;

    public Resume uploadResume(MultipartFile file, String userEmail) throws IOException {

        System.out.println("STEP 1 - Finding user");
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        System.out.println("STEP 2 - User found: " + user.getEmail());
        String storedFileName = fileStorageService.uploadFile(file);

        System.out.println("STEP 3 - File uploaded to S3: " + storedFileName);
        String fileUrl = String.format(
                "https://%s.s3.%s.amazonaws.com/resumes/%s",
                bucketName,
                region,
                storedFileName
        );

        Resume resume = Resume.builder()
                .user(user)
                .originalFileName(file.getOriginalFilename())
                .storedFileName(storedFileName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .fileUrl(fileUrl)
                .build();

        System.out.println("STEP 4 - Resume object created");
        Resume savedResume = resumeRepository.save(resume);

        System.out.println("STEP 5 - Resume saved with ID: " + savedResume.getId());

        // --- RE-ENABLED PYTHON AI CALL ---
       System.out.println("STEP 6 - Calling Python AI Service...");
        try {
            WebClient webClient = WebClient.create("http://localhost:8000");
            
            // Generate the temporary VIP pass for Python
            String presignedUrl = fileStorageService.generatePresignedUrl(savedResume.getStoredFileName());

            ResumeAnalysisRequest aiRequest = ResumeAnalysisRequest.builder()
                    .resumeId(savedResume.getId())
                    .fileUrl(presignedUrl) // <-- SEND THE VIP URL HERE!
                    .fileType(savedResume.getFileType())
                    .build();

           ResumeAnalysisResponse aiResponse = webClient.post()
                    .uri("/api/ai/analyze")
                    .bodyValue(aiRequest)
                    .retrieve()
                    .bodyToMono(ResumeAnalysisResponse.class)
                    .block(); 

            // FETCH A FRESH MANAGED ENTITY BEFORE UPDATING
// FETCH A FRESH MANAGED ENTITY BEFORE UPDATING
            if (aiResponse != null && aiResponse.getAtsScore() != null) {
                Resume entityToUpdate = resumeRepository.findById(savedResume.getId()).orElse(savedResume);
                
                entityToUpdate.setAtsScore(aiResponse.getAtsScore());
                entityToUpdate.setSkillsExtracted(aiResponse.getSkillsExtracted());
                entityToUpdate.setMissingSkills(aiResponse.getMissingSkills());
                entityToUpdate.setSuggestions(aiResponse.getSuggestions());
                
                // IMPORTANT: Reassign the savedResume so the controller gets the new data!
                savedResume = resumeRepository.save(entityToUpdate); 
                System.out.println("STEP 7 - AI Analysis complete and saved.");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to get AI Analysis from Python: " + e.getMessage());
        }

        return savedResume;
    }

   @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> getUserResumesAsMap(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<Resume> resumes = resumeRepository.findByUserOrderByCreatedAtDesc(user);
        
        // Convert the database entities into safe Maps INSIDE the transaction
        return resumes.stream()
            .map(resume -> java.util.Map.<String, Object>of(
                "id", resume.getId(),
                "originalFileName", resume.getOriginalFileName(),
                "fileUrl", resume.getFileUrl(),
                "atsScore", resume.getAtsScore() != null ? resume.getAtsScore() : 0.0,
                // List.copyOf() forces Hibernate to load the data right now
                "skillsExtracted", resume.getSkillsExtracted() != null ? List.copyOf(resume.getSkillsExtracted()) : List.of(),
                "missingSkills", resume.getMissingSkills() != null ? List.copyOf(resume.getMissingSkills()) : List.of(),
                "suggestions", resume.getSuggestions() != null ? List.copyOf(resume.getSuggestions()) : List.of(),
                "createdAt", resume.getCreatedAt()
            ))
            .toList();
    }
}