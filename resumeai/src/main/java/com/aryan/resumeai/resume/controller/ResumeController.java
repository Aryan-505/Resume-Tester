package com.aryan.resumeai.resume.controller;

import com.aryan.resumeai.common.response.ApiResponse;
import com.aryan.resumeai.resume.entity.Resume;
import com.aryan.resumeai.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostConstruct
public void testStartup() {
    System.out.println("RESUME CONTROLLER LOADED");
}

 @PostMapping("/upload")
public ResponseEntity<?> uploadResume(
        @RequestParam("file") MultipartFile file,
        Authentication authentication) {

            
    System.out.println("===== CONTROLLER HIT =====");

    try {

        System.out.println("Authenticated User = " + authentication.getName());
        System.out.println("File Name = " + file.getOriginalFilename());

        Resume savedResume =
                resumeService.uploadResume(
                        file,
                        authentication.getName()
                );

        System.out.println("===== UPLOAD SUCCESS =====");
        System.out.println("Resume ID = " + savedResume.getId());

        return ResponseEntity.ok(
                java.util.Map.of(
                        "success", true,
                        "message", "Resume uploaded successfully",
                        "resumeId", savedResume.getId(),
                        "fileName", savedResume.getOriginalFileName()
                )
        );

    } catch (Exception e) {

        System.out.println("===== UPLOAD FAILED =====");
        e.printStackTrace();

        return ResponseEntity.internalServerError().body(
                java.util.Map.of(
                        "success", false,
                        "message", e.toString()
                )
        );
    }
}
@GetMapping("/my-resumes")
    public ResponseEntity<ApiResponse<List<java.util.Map<String, Object>>>> getMyResumes(Authentication authentication) {
        
        // Get the already-safe mapped data directly from the service
        List<java.util.Map<String, Object>> safeResumes = resumeService.getUserResumesAsMap(authentication.getName());
        
        return ResponseEntity.ok(ApiResponse.<List<java.util.Map<String, Object>>>builder()
                .success(true)
                .message("Resumes fetched successfully")
                .data(safeResumes)
                .build());
    }


}